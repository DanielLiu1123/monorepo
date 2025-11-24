#!/usr/bin/env bash

cmd_test() {
    if [ ! -f "$PROJECT_DIR/build.sh" ]; then
        print_error "No build.sh found in $PROJECT_DIR"
        return 1
    fi

    print_info "Testing $PROJECT_DIR..."

    # Source the project's build.sh and call the test function
    # shellcheck disable=SC1090
    source "$PROJECT_DIR/build.sh"

    if declare -f test > /dev/null 2>&1; then
        test || return 1
    else
        print_error "No 'test' function found in $PROJECT_DIR/build.sh"
        return 1
    fi

    print_success "Tests passed for $PROJECT_DIR"
}
