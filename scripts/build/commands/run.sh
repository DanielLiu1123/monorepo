#!/usr/bin/env bash

cmd_run() {
    if [ ! -f "$PROJECT_DIR/build.sh" ]; then
        print_error "No build.sh found in $PROJECT_DIR"
        return 1
    fi

    print_info "Running $PROJECT_DIR..."

    # Source the project's build.sh and call the run function
    # shellcheck disable=SC1090
    source "$PROJECT_DIR/build.sh"

    if declare -f run > /dev/null 2>&1; then
        run || return 1
    else
        print_error "No 'run' function found in $PROJECT_DIR/build.sh"
        return 1
    fi
}
