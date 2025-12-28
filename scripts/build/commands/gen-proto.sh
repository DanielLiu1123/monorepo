#!/usr/bin/env bash

# Get all proto packages (directories in proto dir that contain .proto files)
get_proto_packages() {
  local proto_dir="$1"

  # Find all .proto files, extract directory, and normalize
  # e.g., monorepo/user/v1/user.proto -> monorepo/user
  # e.g., foo/bar/v1/api.proto -> foo/bar
  find "$proto_dir" -name "*.proto" -type f 2>/dev/null | \
    sed "s|^$proto_dir/||" | \
    rev | cut -d'/' -f3- | rev | \
    sort -u
}

# Setup Java module for a package (create build.gradle and build.sh if needed)
setup_java_module() {
  local package_rel_path="$1" # e.g., "monorepo/user" or "foo/bar"
  local java_module_dir="$ROOT_DIR/packages/proto-gen-java/$package_rel_path"
  local build_gradle_tpl="$ROOT_DIR/packages/proto-gen-java/build.gradle.tpl"
  local build_sh_tpl="$ROOT_DIR/packages/proto-gen-java/build.sh.tpl"
  local module_build_gradle="$java_module_dir/build.gradle"
  local module_build_sh="$java_module_dir/build.sh"

  # Create module directory if it doesn't exist
  if [ ! -d "$java_module_dir" ]; then
    mkdir -p "$java_module_dir"
    print_info "Created Java module directory: $package_rel_path"
  fi

  # Create build.gradle from template if it doesn't exist
  if [ ! -f "$module_build_gradle" ]; then
    if [ ! -f "$build_gradle_tpl" ]; then
      print_warning "Template file not found: $build_gradle_tpl"
      return 1
    fi

    cp "$build_gradle_tpl" "$module_build_gradle"
    print_success "Created build.gradle for $package_rel_path from template"
  fi

  # Create build.sh from template if it doesn't exist
  if [ ! -f "$module_build_sh" ]; then
    if [ ! -f "$build_sh_tpl" ]; then
      print_warning "Template file not found: $build_sh_tpl"
      return 1
    fi

    cp "$build_sh_tpl" "$module_build_sh"
    chmod +x "$module_build_sh"
    print_success "Created build.sh for $package_rel_path from template"
  fi

  return 0
}

# Update proto-gen-java/monorepo/build.gradle (or equivalent parent) to include the module
update_parent_build_gradle() {
  local package_rel_path="$1" # e.g., "monorepo/user"
  local first_part=$(echo "$package_rel_path" | cut -d'/' -f1)
  local parent_build_gradle="$ROOT_DIR/packages/proto-gen-java/$first_part/build.gradle"
  
  # If it's a deep path like foo/bar/baz, we still register it in foo/build.gradle
  # assuming each first-level directory has a build.gradle
  
  # Ensure first_part has a build.gradle
  if [ ! -f "$parent_build_gradle" ]; then
    # If parent build.gradle doesn't exist, we might need to create a simple one or skip
    # For now, let's assume the structure exists or we skip registration if it's not a known pattern
    # In monorepo, packages/proto-gen-java/monorepo/build.gradle exists.
    # If someone adds 'foo/bar', we might need packages/proto-gen-java/foo/build.gradle
    print_warning "Parent build.gradle not found: $parent_build_gradle. Skipping dependency registration."
    return 0
  fi

  local gradle_path=$(echo "$package_rel_path" | sed 's|/$||' | tr '/' ':')
  local module_reference="api project(\":packages:proto-gen-java:$gradle_path\")"

  # Check if module is already in build.gradle
  if grep -q ":$gradle_path" "$parent_build_gradle" 2>/dev/null; then
    return 0
  fi

  # Add module to dependencies
  # Insert before the closing brace of dependencies block
  sed -i '' "/^dependencies {/a\\
    $module_reference
" "$parent_build_gradle"
  print_success "Added $gradle_path to $parent_build_gradle"

  return 0
}

# Update settings.gradle to include the module
update_settings_gradle() {
  local package_rel_path="$1" # e.g., "monorepo/user"
  local settings_gradle="$ROOT_DIR/settings.gradle"
  local gradle_path=$(echo "$package_rel_path" | sed 's|/$||' | tr '/' ':')
  local module_include="include \":packages:proto-gen-java:$gradle_path\""

  # Check if module is already in settings.gradle
  if grep -q "proto-gen-java:$gradle_path\"" "$settings_gradle" 2>/dev/null; then
    return 0
  fi

  # Find the last proto-gen-java include line and add after it
  if [ -f "$settings_gradle" ]; then
    # Find line number of last proto-gen-java include
    local last_line=$(grep -n "include \":packages:proto-gen-java" "$settings_gradle" | tail -1 | cut -d: -f1)

    if [ -n "$last_line" ]; then
      # Insert after the last proto-gen-java include
      sed -i '' "${last_line}a\\
$module_include
" "$settings_gradle"
    else
      # If no proto-gen-java includes found, add after the main proto-gen-java include
      sed -i '' "/include \":packages:proto-gen-java\"/a\\
$module_include
" "$settings_gradle"
    fi

    print_success "Added $gradle_path to settings.gradle"
  else
    print_warning "settings.gradle not found: $settings_gradle"
    return 1
  fi

  return 0
}

# Generate code for a specific package
generate_package() {
  local proto_dir="$1"
  local package_rel_path="$2" # e.g., "monorepo/user"
  local filter_path="$3"      # e.g., "monorepo/user/v1"

  local template_file="buf.gen._p0_._p1_.yaml"
  local temp_config_file=".buf.gen.$(echo "$package_rel_path" | tr '/' '_').tmp.yaml"

  # Check if template exists
  if [ ! -f "$proto_dir/$template_file" ]; then
    print_error "Template file not found: $template_file"
    return 1
  fi

  print_info "Generating code for package: $package_rel_path"

  # Split package_rel_path into p0 and p1
  # p0 = first directory, p1 = rest of the path
  local p0=$(echo "$package_rel_path" | cut -d'/' -f1)
  local p1=$(echo "$package_rel_path" | cut -d'/' -f2-)
  
  # If there is no slash, p1 will be same as p0. We want p1 to be empty or properly handled.
  if [ "$p0" = "$p1" ]; then
    p1=""
    # If p1 is empty, we need to decide how the template handles it.
    # The template uses ${p0}/${p1}. If p1 is empty, it results in ${p0}/
    # Let's adjust p1 to be something sensible or fix the template.
    # Actually, if we have "monorepo/user", p0=monorepo, p1=user.
    # If we have just "common", p0=common, p1="" -> this might be a problem for the template.
    # But based on the requirement, it should be at least two levels or we handle it.
  fi

  # Create temporary config file by replacing ${p0} and ${p1}
  sed -e "s|\${p0}|$p0|g" -e "s|\${p1}|$p1|g" "$proto_dir/$template_file" > "$proto_dir/$temp_config_file"
  trap "rm -f $proto_dir/$temp_config_file" EXIT

  # Build buf generate command with package path filter
  local buf_cmd="buf generate --template \"$temp_config_file\" --path \"$filter_path\""

  # Generate proto code
  local result=0
  if cd "$proto_dir" && eval "$buf_cmd"; then
    print_success "Package $package_rel_path generated successfully"

    # Setup Java module (create build.gradle if needed and register in parent files)
    setup_java_module "$package_rel_path"
    update_parent_build_gradle "$package_rel_path"
    update_settings_gradle "$package_rel_path"
  else
    print_error "Failed to generate package: $package_rel_path"
    result=1
  fi

  return $result
}

# Find proto packages affected by git changes
find_affected_proto_packages() {
  local changed_files
  changed_files=$(get_git_changed_files)
  
  if [ -z "$changed_files" ]; then
    return 0
  fi
  
  local proto_dir_rel="packages/proto"
  local affected_packages=()
  
  # For each changed file, check if it's a proto file and find its package
  while IFS= read -r file; do
    # Check if file is under packages/proto and ends with .proto
    if [[ "$file" == "$proto_dir_rel"/*.proto ]]; then
       # It's a proto file in the root or some depth, but we need at least 2 levels for p0/p1
       # However, get_proto_packages logic is:
       # find "$proto_dir" -name "*.proto" -type f | sed "s|^$proto_dir/||" | rev | cut -d'/' -f3- | rev | sort -u
       # This means for packages/proto/monorepo/user/v1/user.proto, it extracts "monorepo/user"
       
       local rel_to_proto=${file#$proto_dir_rel/}
       local package_rel_path=$(echo "$rel_to_proto" | rev | cut -d'/' -f3- | rev)
       
       if [ -n "$package_rel_path" ]; then
         affected_packages+=("$package_rel_path")
       fi
    fi
  done <<< "$changed_files"
  
  # Sort and output unique packages
  if [ ${#affected_packages[@]} -gt 0 ]; then
    printf "%s\n" "${affected_packages[@]}" | sort -u
  fi
}

cmd_gen_proto() {
  local target_path="$1" # Relative to packages/proto, e.g., "monorepo/user"
  local proto_dir="$ROOT_DIR/packages/proto"

  if [ ! -d "$proto_dir" ]; then
    print_error "Proto directory not found: $proto_dir"
    return 1
  fi

  local packages_to_generate=()

  # Determine which packages to generate based on path
  if [ -z "$target_path" ]; then
    # No path specified, detect projects based on git changes
    print_info "No path specified, detecting proto packages based on git changes..."
    local affected
    affected=$(find_affected_proto_packages)
    
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
    # Generate all packages
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

    # Build package list (package_rel_path:filter_path)
    while IFS= read -r pkg; do
      packages_to_generate+=("$pkg:$pkg")
    done <<< "$all_packages"
  else
    # Path explicitly provided
    print_info "Generating code for specified path: $target_path"

    # Count levels in target_path
    local levels=$(echo "$target_path" | tr -cd '/' | wc -c | tr -d ' ')
    
    if [ "$levels" -eq 0 ]; then
      # Only one level specified (e.g., "foo")
      # Find all two-level packages under this directory
      local sub_packages=$(find "$proto_dir/$target_path" -maxdepth 1 -mindepth 1 -type d 2>/dev/null)
      if [ -n "$sub_packages" ]; then
        while IFS= read -r sub_dir; do
          local rel_sub_dir=${sub_dir#$proto_dir/}
          # Only add if it contains proto files at any depth
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
      # Two or more levels specified
      # Always extract the package_rel_path as the first two levels of the path
      # e.g., monorepo/user/v1 -> monorepo/user
      # e.g., foo/bar/v1/api.proto -> foo/bar
      local package_rel_path=$(echo "$target_path" | cut -d'/' -f1,2)
      
      # Check if target_path points to a specific file or directory
      if [ -f "$proto_dir/$target_path" ]; then
        # It's a file, e.g., monorepo/user/v1/user.proto
        packages_to_generate+=("$package_rel_path:$target_path")
      elif [ -d "$proto_dir/$target_path" ]; then
        # It's a directory, e.g., monorepo/user/v1
        packages_to_generate+=("$package_rel_path:$target_path")
      else
        # Maybe target_path is just a package name that doesn't exist as a directory directly 
        # but contains proto files in subdirectories
        if find "$proto_dir/$target_path" -name "*.proto" -type f | grep -q .; then
           packages_to_generate+=("$package_rel_path:$target_path")
        else
           print_error "No proto files found under: $target_path"
           return 1
        fi
      fi
    fi
  fi

  # Generate code for each package (in parallel)
  local total=${#packages_to_generate[@]}

  if [ $total -eq 0 ]; then
    print_info "No packages to generate"
    return 0
  fi

  print_info "Generating code for $total package(s) in parallel..."

  # Create temporary directory for tracking results
  local tmp_dir=$(mktemp -d)
  trap "rm -rf $tmp_dir" EXIT
  local pids=()

  # Launch parallel generation tasks
  for pkg_info in "${packages_to_generate[@]}"; do
    local package_name="${pkg_info%%:*}"
    local package_path="${pkg_info#*:}"

    # Run in background and track PID
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

  # Wait for all background jobs to complete
  for pid in "${pids[@]}"; do
    wait "$pid"
  done

  # Count results
  local success=$(find "$tmp_dir" -name "*.success" 2>/dev/null | wc -l | tr -d ' ')
  local failed=$(find "$tmp_dir" -name "*.failed" 2>/dev/null | wc -l | tr -d ' ')

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
