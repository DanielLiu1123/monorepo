#!/usr/bin/env bash

# Get all proto packages (top-level directories in proto dir that contain .proto files)
get_proto_packages() {
  local proto_dir="$1"

  # Find all .proto files and extract the first-level directory (package name)
  find "$proto_dir" -name "*.proto" -type f | \
    sed "s|^$proto_dir/||" | \
    awk -F'/' '{print $1}' | \
    sort -u
}

# Setup Java module for a package (create build.gradle and build.sh if needed)
setup_java_module() {
  local package_name="$1"
  local java_module_dir="$ROOT_DIR/packages/proto-gen-java/proto-$package_name"
  local build_gradle_tpl="$ROOT_DIR/packages/proto-gen-java/build.gradle.tpl"
  local build_sh_tpl="$ROOT_DIR/packages/proto-gen-java/build.sh.tpl"
  local module_build_gradle="$java_module_dir/build.gradle"
  local module_build_sh="$java_module_dir/build.sh"

  # Create module directory if it doesn't exist
  if [ ! -d "$java_module_dir" ]; then
    mkdir -p "$java_module_dir"
    print_info "Created Java module directory: proto-$package_name"
  fi

  # Create build.gradle from template if it doesn't exist
  if [ ! -f "$module_build_gradle" ]; then
    if [ ! -f "$build_gradle_tpl" ]; then
      print_warning "Template file not found: $build_gradle_tpl"
      return 1
    fi

    cp "$build_gradle_tpl" "$module_build_gradle"
    print_success "Created build.gradle for proto-$package_name from template"
  fi

  # Create build.sh from template if it doesn't exist
  if [ ! -f "$module_build_sh" ]; then
    if [ ! -f "$build_sh_tpl" ]; then
      print_warning "Template file not found: $build_sh_tpl"
      return 1
    fi

    cp "$build_sh_tpl" "$module_build_sh"
    chmod +x "$module_build_sh"
    print_success "Created build.sh for proto-$package_name from template"
  fi

  return 0
}

# Update proto-gen-java/build.gradle to include the module
update_parent_build_gradle() {
  local package_name="$1"
  local parent_build_gradle="$ROOT_DIR/packages/proto-gen-java/build.gradle"
  local module_reference="api project(\":packages:proto-gen-java:proto-$package_name\")"

  # Check if module is already in build.gradle
  if grep -q "proto-$package_name" "$parent_build_gradle" 2>/dev/null; then
    return 0
  fi

  # Add module to dependencies
  if [ -f "$parent_build_gradle" ]; then
    # Insert before the closing brace of dependencies block
    sed -i '' "/^dependencies {/a\\
    $module_reference
" "$parent_build_gradle"
    print_success "Added proto-$package_name to proto-gen-java/build.gradle"
  else
    print_warning "Parent build.gradle not found: $parent_build_gradle"
    return 1
  fi

  return 0
}

# Update settings.gradle to include the module
update_settings_gradle() {
  local package_name="$1"
  local settings_gradle="$ROOT_DIR/settings.gradle"
  local module_include="include \":packages:proto-gen-java:proto-$package_name\""

  # Check if module is already in settings.gradle
  if grep -q "proto-gen-java:proto-$package_name" "$settings_gradle" 2>/dev/null; then
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

    print_success "Added proto-$package_name to settings.gradle"
  else
    print_warning "settings.gradle not found: $settings_gradle"
    return 1
  fi

  return 0
}

# Generate code for a specific package
generate_package() {
  local proto_dir="$1"
  local package_name="$2"
  local package_path="$3"

  local template_file="buf.gen._pkg_.yaml"
  local temp_config_file=".buf.gen.${package_name}.tmp.yaml"

  # Check if template exists
  if [ ! -f "$proto_dir/$template_file" ]; then
    print_error "Template file not found: $template_file"
    return 1
  fi

  print_info "Generating code for package: $package_name"

  # Create temporary config file by replacing ${package} placeholder
  sed "s/\${package}/$package_name/g" "$proto_dir/$template_file" > "$proto_dir/$temp_config_file"

  # Build buf generate command with package path filter
  local buf_cmd="buf generate --template \"$temp_config_file\" --path \"$package_path\""

  # Generate proto code
  local result=0
  if cd "$proto_dir" && eval "$buf_cmd"; then
    print_success "Package $package_name generated successfully"

    # Setup Java module (create build.gradle if needed and register in parent files)
    setup_java_module "$package_name"
    update_parent_build_gradle "$package_name"
    update_settings_gradle "$package_name"
  else
    print_error "Failed to generate package: $package_name"
    result=1
  fi

  # Clean up temporary config file
  rm -f "$proto_dir/$temp_config_file"

  return $result
}

cmd_gen_proto() {
  local target_path="$1"
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
  if [ ! -e "$proto_dir/$target_path" ]; then
    print_error "Path not found: $target_path"
    return 1
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

    # Build package list
    while IFS= read -r pkg; do
      packages_to_generate+=("$pkg:$pkg")
    done <<< "$all_packages"
  else
    # Path explicitly provided (specific package or path within package)
    print_info "Generating code for specified path: $target_path"

    # Extract package name from path (first directory component)
    local package_name=$(echo "$target_path" | awk -F'/' '{print $1}')

    if [ -n "$package_name" ]; then
      packages_to_generate+=("$package_name:$target_path")
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
  local pids=()

  # Launch parallel generation tasks
  for pkg_info in "${packages_to_generate[@]}"; do
    local package_name="${pkg_info%%:*}"
    local package_path="${pkg_info#*:}"

    # Run in background and track PID
    (
      if generate_package "$proto_dir" "$package_name" "$package_path"; then
        touch "$tmp_dir/$package_name.success"
      else
        touch "$tmp_dir/$package_name.failed"
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

  # Clean up temporary directory
  rm -rf "$tmp_dir"

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
