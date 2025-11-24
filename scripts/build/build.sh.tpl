#!/usr/bin/env bash

# Project-Level Build Script Template
#
# This file allows you to customize build behavior for a specific project.
# Place this file as `build.sh` in your project root directory.
#
# You can define any of the following functions to override default behavior:
#   - clean
#   - build
#   - fmt
#   - install
#   - lint
#   - run
#   - test
#
# Available environment variables:
#   - ROOT_DIR     - The root directory of the monorepo
#   - PROJECT_DIR  - The directory of the current project (absolute path)
#
# You have access to all utility functions from main.sh:
#   - print_info "message"    - Print info message in blue
#   - print_success "message" - Print success message in green
#   - print_error "message"   - Print error message in red
#   - print_warning "message" - Print warning message in yellow
#   - execute_command "cmd"   - Execute and print command

# Example: Custom build function
# Uncomment and modify as needed
# build() {
#     print_info "Running custom build for $PROJECT_DIR"
#
#     # Your custom build logic here
#     execute_command "cd $PROJECT_DIR && npm run build:custom"
#
#     return $?
# }

# Example: Custom clean function
# clean() {
#     print_info "Running custom clean for $PROJECT_DIR"
#
#     # Your custom clean logic here
#     execute_command "rm -rf $PROJECT_DIR/dist $PROJECT_DIR/build"
#
#     return $?
# }

# Example: Custom fmt function
# fmt() {
#     print_info "Running custom fmt for $PROJECT_DIR"
#
#     # Your custom fmt logic here
#     execute_command "cd $PROJECT_DIR && prettier --write ."
#
#     return $?
# }

# Example: Custom install function
# install() {
#     print_info "Running custom install for $PROJECT_DIR"
#
#     # Your custom install logic here
#     execute_command "cd $PROJECT_DIR && pnpm install"
#
#     return $?
# }

# Example: Custom lint function
# lint() {
#     print_info "Running custom lint for $PROJECT_DIR"
#
#     # Your custom lint logic here
#     execute_command "cd $PROJECT_DIR && npm run lint:custom"
#
#     return $?
# }

# Example: Custom run function
# run() {
#     print_info "Running custom run for $PROJECT_DIR"
#
#     # Your custom run logic here
#     execute_command "cd $PROJECT_DIR && npm run dev:custom"
#
#     return $?
# }

# Example: Custom test function
# test() {
#     print_info "Running custom test for $PROJECT_DIR"
#
#     # Your custom test logic here
#     execute_command "cd $PROJECT_DIR && npm run test:e2e"
#
#     return $?
# }

# Example: Complex custom build with multiple steps
# build() {
#     print_info "Running multi-step custom build for $PROJECT_DIR"
#
#     # Step 1: Generate code
#     print_info "Step 1: Generating code..."
#     execute_command "cd $PROJECT_DIR && npm run codegen" || return 1
#
#     # Step 2: Build
#     print_info "Step 2: Building..."
#     execute_command "cd $PROJECT_DIR && npm run build" || return 1
#
#     # Step 3: Post-build processing
#     print_info "Step 3: Post-build processing..."
#     execute_command "cd $PROJECT_DIR && npm run postbuild" || return 1
#
#     print_success "Custom build completed successfully"
#     return 0
# }
