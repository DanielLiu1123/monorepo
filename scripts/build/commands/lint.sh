#!/usr/bin/env bash

cmd_lint() {
    if [ ! -f "$PROJECT_DIR/build.sh" ]; then
        print_error "No build.sh found in $PROJECT_DIR"
        return 1
    fi

    print_info "Linting $PROJECT_DIR..."

    # Source the project's build.sh and call the lint function
    # shellcheck disable=SC1090
    source "$PROJECT_DIR/build.sh"

    if declare -f lint > /dev/null 2>&1; then
        lint || return 1
    else
        print_error "No 'lint' function found in $PROJECT_DIR/build.sh"
        return 1
    fi

    print_success "Linting completed successfully for $PROJECT_DIR"
}
