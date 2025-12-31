#!/usr/bin/env bash

install() {
    execute_cmd "cd $PROJECT_DIR && go mod download"
    execute_cmd "go work sync"
}

build() {
    execute_cmd "cd $PROJECT_DIR && go build ./..."
}

test() {
    print_info "Skipping tests for generated code"
}

lint() {
    print_info "Skipping lint for generated code"
}

fmt() {
    print_info "Skipping fmt for generated code"
}

clean() {
    print_info "Nothing to clean for generated code"
}

run() {
    print_info "Nothing to run for generated code"
}
