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

cmd_gen_proto() {
  local target_path="$1" # Relative to packages/proto, e.g., "monorepo/user"
  local proto_dir="$ROOT_DIR/packages/proto"

  if [ ! -d "$proto_dir" ]; then
    print_error "Proto directory not found: $proto_dir"
    return 1
  fi

  local packages_to_generate=()

  # Normalize target_path: if empty, default to "." (all packages)
  if [ -z "$target_path" ]; then
    target_path="."
  fi

  # Validate the path exists
  if [ "$target_path" != "." ]; then
    if [ ! -e "$proto_dir/$target_path" ]; then
      print_error "Path not found: $target_path"
      return 1
    fi
  fi

  # Determine which packages to generate based on path
  if [ "$target_path" = "." ]; then
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

    # If target_path is a directory containing .proto files (or versions), 
    # we need to find the "package_rel_path"
    
    # Check if target_path points to a specific file or directory
    if [ -f "$proto_dir/$target_path" ]; then
      # It's a file, e.g., monorepo/user/v1/user.proto
      local rel_dir=$(dirname "$target_path")
      local package_rel_path=$(echo "$rel_dir" | rev | cut -d'/' -f2- | rev)
      packages_to_generate+=("$package_rel_path:$target_path")
    else
      # It's a directory
      # Find all proto files under this directory and group them by package
      local found_packages=$(get_proto_packages "$proto_dir/$target_path" | sed "s|^|$target_path/|")
      
      if [ -n "$found_packages" ]; then
         while IFS= read -r pkg; do
            packages_to_generate+=("$pkg:$pkg")
         done <<< "$found_packages"
      else
         # Maybe the target_path itself is (part of) a package path but doesn't have protos directly
         # e.g., "monorepo/user" -> we want to find protos under it
         # Use find to see if there are any protos
         if find "$proto_dir/$target_path" -name "*.proto" -type f | grep -q .; then
            # Protos exist, let's find the package paths
            local sub_packages=$(find "$proto_dir/$target_path" -name "*.proto" -type f | \
                sed "s|^$proto_dir/||" | \
                rev | cut -d'/' -f3- | rev | \
                sort -u)
            while IFS= read -r pkg; do
                packages_to_generate+=("$pkg:$pkg")
            done <<< "$sub_packages"
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
