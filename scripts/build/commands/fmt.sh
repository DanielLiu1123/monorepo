#!/usr/bin/env bash

cmd_fmt() {
    if [ ! -f "$PROJECT_DIR/build.sh" ]; then
        print_error "No build.sh found in $PROJECT_DIR"
        return 1
    fi

    print_info "Formatting $PROJECT_DIR..."

    # Source the project's build.sh and call the fmt function
    # shellcheck disable=SC1090
    source "$PROJECT_DIR/build.sh"

    if declare -f fmt > /dev/null 2>&1; then
        fmt || return 1
    else
        print_error "No 'fmt' function found in $PROJECT_DIR/build.sh"
        return 1
    fi

    print_success "Formatting completed successfully for $PROJECT_DIR"
}
