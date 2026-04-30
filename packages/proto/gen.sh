#!/usr/bin/env bash

get_proto_packages() {
  local proto_dir="$1"

  find "$proto_dir" -name "*.proto" -type f 2>/dev/null | \
    sed "s|^$proto_dir/||" | \
    rev | cut -d'/' -f3- | rev | \
    sort -u
}

generate_package() {
  local proto_dir="$1"
  local package_rel_path="$2"
  local filter_path="$3"

  local template_file="buf.gen._p0_._p1_.yaml"
  local temp_config_file=".buf.gen.$(echo "$package_rel_path" | tr '/' '_').tmp.yaml"

  if [ ! -f "$proto_dir/$template_file" ]; then
    print_error "Template file not found: $template_file"
    return 1
  fi

  print_info "Generating code for package: $package_rel_path"

  local p0=$(echo "$package_rel_path" | cut -d'/' -f1)
  local p1=$(echo "$package_rel_path" | cut -d'/' -f2-)

  if [ "$p0" = "$p1" ]; then
    p1=""
  fi

  sed -e "s|\${p0}|$p0|g" -e "s|\${p1}|$p1|g" "$proto_dir/$template_file" > "$proto_dir/$temp_config_file"
  trap "rm -f $proto_dir/$temp_config_file" EXIT

  local buf_cmd="buf generate --template \"$temp_config_file\" --path \"$filter_path\""

  local result=0
  if cd "$proto_dir" && eval "$buf_cmd"; then
    print_success "Package $package_rel_path generated successfully"
  else
    print_error "Failed to generate package: $package_rel_path"
    result=1
  fi

  return $result
}

find_affected_proto_packages() {
  local proto_dir="$1"
  local changed_files
  changed_files=$(get_git_changed_files)

  if [ -z "$changed_files" ]; then
    return 0
  fi

  local proto_dir_rel
  proto_dir_rel=$(realpath --relative-to="$ROOT_DIR" "$proto_dir" 2>/dev/null || echo "packages/proto")

  local affected_packages=()

  while IFS= read -r file; do
    if [[ "$file" == "$proto_dir_rel"/*.proto ]]; then
      local rel_to_proto=${file#$proto_dir_rel/}
      local package_rel_path=$(echo "$rel_to_proto" | rev | cut -d'/' -f3- | rev)

      if [ -n "$package_rel_path" ]; then
        affected_packages+=("$package_rel_path")
      fi
    fi
  done <<< "$changed_files"

  if [ ${#affected_packages[@]} -gt 0 ]; then
    printf "%s\n" "${affected_packages[@]}" | sort -u
  fi
}

run_gen() {
  local target_path="$1"
  local proto_dir="$PROJECT_DIR"

  if [ ! -d "$proto_dir" ]; then
    print_error "Proto directory not found: $proto_dir"
    return 1
  fi

  local packages_to_generate=()

  if [ -z "$target_path" ]; then
    print_info "No path specified, detecting proto packages based on git changes..."
    local affected
    affected=$(find_affected_proto_packages "$proto_dir")

    if [ -z "$affected" ]; then
      print_warning "No proto changes detected, nothing to generate"
      return 0
    fi

    print_info ""
    print_info "Detected changed proto packages:"
    while IFS= read -r pkg; do
      print_info " - $pkg"
      packages_to_generate+=("$pkg:$pkg")
    done <<< "$affected"
  elif [ "$target_path" = "." ]; then
    local all_packages
    all_packages=$(get_proto_packages "$proto_dir")

    if [ -z "$all_packages" ]; then
      print_info "No proto packages found"
      return 0
    fi

    print_info "Generating code for all packages..."
    print_info "Found proto packages:"
    echo "$all_packages" | while read -r pkg; do
      echo "  - $pkg"
    done

    while IFS= read -r pkg; do
      packages_to_generate+=("$pkg:$pkg")
    done <<< "$all_packages"
  else
    print_info "Generating code for specified path: $target_path"

    local levels=$(echo "$target_path" | tr -cd '/' | wc -c | tr -d ' ')

    if [ "$levels" -eq 0 ]; then
      local sub_packages
      sub_packages=$(find "$proto_dir/$target_path" -maxdepth 1 -mindepth 1 -type d 2>/dev/null)
      if [ -n "$sub_packages" ]; then
        while IFS= read -r sub_dir; do
          local rel_sub_dir=${sub_dir#$proto_dir/}
          if find "$sub_dir" -name "*.proto" -type f | grep -q .; then
            packages_to_generate+=("$rel_sub_dir:$rel_sub_dir")
          fi
        done <<< "$sub_packages"
      fi

      if [ ${#packages_to_generate[@]} -eq 0 ]; then
        print_error "No proto packages found under: $target_path"
        return 1
      fi
    else
      local package_rel_path=$(echo "$target_path" | cut -d'/' -f1,2)

      if [ -f "$proto_dir/$target_path" ]; then
        packages_to_generate+=("$package_rel_path:$target_path")
      elif [ -d "$proto_dir/$target_path" ]; then
        packages_to_generate+=("$package_rel_path:$target_path")
      else
        if find "$proto_dir/$target_path" -name "*.proto" -type f 2>/dev/null | grep -q .; then
          packages_to_generate+=("$package_rel_path:$target_path")
        else
          print_error "No proto files found under: $target_path"
          return 1
        fi
      fi
    fi
  fi

  local total=${#packages_to_generate[@]}

  if [ $total -eq 0 ]; then
    print_info "No packages to generate"
    return 0
  fi

  print_info "Generating code for $total package(s) in parallel..."

  local tmp_dir
  tmp_dir=$(mktemp -d)
  trap "rm -rf $tmp_dir" EXIT
  local pids=()

  for pkg_info in "${packages_to_generate[@]}"; do
    local package_name="${pkg_info%%:*}"
    local package_path="${pkg_info#*:}"

    (
      local safe_pkg_name=$(echo "$package_name" | tr '/' '_')
      if generate_package "$proto_dir" "$package_name" "$package_path"; then
        touch "$tmp_dir/${safe_pkg_name}.success"
      else
        touch "$tmp_dir/${safe_pkg_name}.failed"
      fi
    ) &
    pids+=($!)
  done

  for pid in "${pids[@]}"; do
    wait "$pid"
  done

  local success
  local failed
  success=$(find "$tmp_dir" -name "*.success" 2>/dev/null | wc -l | tr -d ' ')
  failed=$(find "$tmp_dir" -name "*.failed" 2>/dev/null | wc -l | tr -d ' ')

  echo ""
  echo "================================"
  print_info "Generation Summary: $total package(s)"
  print_success "$success succeeded"

  if [ $failed -gt 0 ]; then
    print_error "$failed failed"
    return 1
  fi

  return 0
}
