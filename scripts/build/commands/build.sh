#!/usr/bin/env bash

cmd_build() {
    if [ ! -f "$PROJECT_DIR/build.sh" ]; then
        print_error "No build.sh found in $PROJECT_DIR"
        return 1
    fi

    print_info "Building $PROJECT_DIR..."

    # Source the project's build.sh and call the build function
    # shellcheck disable=SC1090
    source "$PROJECT_DIR/build.sh"

    if declare -f build > /dev/null 2>&1; then
        build || return 1
    else
        print_error "No 'build' function found in $PROJECT_DIR/build.sh"
        return 1
    fi

    print_success "$PROJECT_DIR built successfully"
}
