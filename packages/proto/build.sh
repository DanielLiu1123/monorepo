#!/usr/bin/env bash

install() {
    execute_cmd "cd $PROJECT_DIR && buf dep update"
}

build() {
    print_info "No build step required for proto definitions"
}

test() {
    print_info "Skipping tests for proto definitions"
}

lint() {
    execute_cmd "cd $PROJECT_DIR && buf lint"
}

fmt() {
    execute_cmd "cd $PROJECT_DIR && buf format --write"
}

clean() {
    print_info "Please manually remove generated code if needed"
}

run() {
    print_info "Nothing to run for proto definitions"
}
