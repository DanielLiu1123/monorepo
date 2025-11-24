#!/usr/bin/env bash

cmd_clean() {
    if [ ! -f "$PROJECT_DIR/build.sh" ]; then
        print_error "No build.sh found in $PROJECT_DIR"
        return 1
    fi

    print_info "Cleaning $PROJECT_DIR..."

    # Source the project's build.sh and call the clean function
    # shellcheck disable=SC1090
    source "$PROJECT_DIR/build.sh"

    if declare -f clean > /dev/null 2>&1; then
        clean || return 1
    else
        print_error "No 'clean' function found in $PROJECT_DIR/build.sh"
        return 1
    fi

    print_success "$PROJECT_DIR cleaned successfully"
}
