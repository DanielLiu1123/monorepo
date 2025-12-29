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
#   - ROOT_DIR         - The root directory of the monorepo
#   - PROJECT_DIR      - The directory of the current project (absolute path)
#   - PROJECT_REL_DIR  - The relative path of the project from ROOT_DIR
#
# You have access to all utility functions from main.sh:
#   - print_info "message"    - Print info message in blue
#   - print_success "message" - Print success message in green
#   - print_error "message"   - Print error message in red
#   - print_warning "message" - Print warning message in yellow
#

# Example: Custom build function for npm project
# build() {
#     cd $PROJECT_DIR && npm run build
# }

# Example: Custom clean function
# clean() {
#     rm -rf $PROJECT_DIR/dist $PROJECT_DIR/build
# }

# Example: Custom fmt function
# fmt() {
#     cd $PROJECT_DIR && prettier --write .
# }

# Example: Custom install function
# install() {
#     cd $PROJECT_DIR && pnpm install
# }

# Example: Custom lint function
# lint() {
#     cd $PROJECT_DIR && npm run lint
# }

# Example: Custom run function
# run() {
#     cd $PROJECT_DIR && npm run dev
# }

# Example: Custom test function
# test() {
#     cd $PROJECT_DIR && npm run test:e2e
# }

# Example: Complex custom build with multiple steps
# build() {
#     print_info "Step 1: Generating code..."
#     cd $PROJECT_DIR && npm run codegen
#
#     print_info "Step 2: Building..."
#     cd $PROJECT_DIR && npm run build
#
#     print_info "Step 3: Post-build processing..."
#     cd $PROJECT_DIR && npm run postbuild
#
#     print_success "Custom build completed successfully"
# }
