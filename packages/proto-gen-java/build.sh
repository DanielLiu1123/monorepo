#!/usr/bin/env bash

# Project-Level Build Script Template
#
# This file allows you to customize build behavior for a specific project.
# Place this file as `build.sh` in your project root directory.
#
# You can define any of the following functions to override default behavior:
#   - clean
#   - build
#   - format
#   - install
#   - lint
#   - run
#   - test
#
# Each function receives the project path as the first argument: $1
#
# You have access to all utility functions from main.sh:
#   - print_info "message"    - Print info message in blue
#   - print_success "message" - Print success message in green
#   - print_error "message"   - Print error message in red
#   - print_warning "message" - Print warning message in yellow
#   - execute_command "cmd"   - Execute and print command
#   - detect_project_type     - Returns: go, gradle, npm, or unknown
#   - detect_project_category - Returns: app or lib

# Example: Custom build function
# Uncomment and modify as needed
# build() {
#     local project_path="$1"
#     print_info "Running custom build for $project_path"
#
#     # Your custom build logic here
#     execute_command "cd $project_path && npm run build:custom"
#
#     return $?
#}

# Example: Custom clean function
# clean() {
#     local project_path="$1"
#     print_info "Running custom clean for $project_path"
#
#     # Your custom clean logic here
#     execute_command "rm -rf $project_path/dist $project_path/build"
#
#     return $?
# }

# Example: Custom format function
format() {
  # Generated code does not need formatting
  print_info "Skipping formatting for generated code"
  return 0
}

# Example: Custom install function
# install() {
#     local project_path="$1"
#     print_info "Running custom install for $project_path"
#
#     # Your custom install logic here
#     execute_command "cd $project_path && pnpm install"
#
#     return $?
# }

# Example: Custom lint function
lint() {
  # Generated code does not need linting
  print_info "Skipping linting for generated code"
  return 0
}

# Example: Custom run function
# run() {
#     local project_path="$1"
#     print_info "Running custom run for $project_path"
#
#     # Your custom run logic here
#     execute_command "cd $project_path && npm run dev:custom"
#
#     return $?
# }

# Example: Custom test function
test() {
  # Generated code does not need testing
  print_info "Skipping tests for generated code"
  return 0
}

# Example: Complex custom build with multiple steps
# build() {
#     local project_path="$1"
#     print_info "Running multi-step custom build for $project_path"
#
#     # Step 1: Generate code
#     print_info "Step 1: Generating code..."
#     execute_command "cd $project_path && npm run codegen" || return 1
#
#     # Step 2: Build
#     print_info "Step 2: Building..."
#     execute_command "cd $project_path && npm run build" || return 1
#
#     # Step 3: Post-build processing
#     print_info "Step 3: Post-build processing..."
#     execute_command "cd $project_path && npm run postbuild" || return 1
#
#     print_success "Custom build completed successfully"
#     return 0
# }
