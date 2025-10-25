#!/usr/bin/env bash

# Get changed proto files from git diff
get_changed_proto_paths() {
  local proto_dir="$1"
  local changed_files

  # Get all changed .proto files (both staged and unstaged)
  # Use --relative to get paths relative to the proto directory
  changed_files=$(cd "$proto_dir" && git diff --name-only --relative HEAD 2>/dev/null | grep '\.proto$')

  if [ -z "$changed_files" ]; then
    # If no changes in working tree, check for untracked files
    # For untracked files, we need to manually strip the proto dir prefix
    local proto_dir_name=$(basename "$proto_dir")
    local proto_parent_dir=$(dirname "$proto_dir")
    changed_files=$(cd "$proto_parent_dir" && git ls-files --others --exclude-standard "$proto_dir_name" 2>/dev/null | grep '\.proto$' | sed "s|^$proto_dir_name/||")
  fi

  if [ -z "$changed_files" ]; then
    return 1
  fi

  # Extract unique directory paths from changed files
  echo "$changed_files" | xargs -n1 dirname | sort -u
}

cmd_gen_proto() {
  local target_path="$1"
  local proto_dir="$ROOT_DIR/packages/proto"

  if [ ! -d "$proto_dir" ]; then
    print_error "Proto directory not found: $proto_dir"
    return 1
  fi

  local buf_args=""

  # Determine which paths to generate
  if [ -n "$target_path" ]; then
    # Path explicitly provided
    print_info "Generating code for specified path: $target_path"

    # Validate the path exists
    if [ ! -e "$proto_dir/$target_path" ]; then
      print_error "Path not found: $target_path"
      return 1
    fi

    # Add --path argument for buf generate
    if [ "$target_path" != "." ] && [ "$target_path" != "./" ]; then
      buf_args="--path \"$target_path\""
    fi
  else
    # No path provided, use git diff to detect changes
    print_info "No path specified, detecting changed proto files..."

    local changed_paths
    # Temporarily disable exit on error for this check
    set +e
    changed_paths=$(get_changed_proto_paths "$proto_dir")
    local diff_status=$?
    set -e

    if [ $diff_status -eq 0 ] && [ -n "$changed_paths" ]; then
      print_info "Found changed proto files in:"
      echo "$changed_paths" | while read -r path; do
        echo "  - $path"
      done

      # Build --path arguments for each changed path
      while IFS= read -r path; do
        buf_args="$buf_args --path \"$path\""
      done <<< "$changed_paths"

      print_info "Using paths: $buf_args"
    else
      print_info "No changed proto files detected, skip to generate."
      exit 0
    fi
  fi

  print_info "Generating code from proto files..."

  # Generate proto code with eval to handle quoted arguments properly
  if cd "$proto_dir" && eval "buf generate $buf_args"; then
    print_success "Proto code generated successfully"
    return 0
  else
    print_error "Failed to generate proto code"
    return 1
  fi
}
