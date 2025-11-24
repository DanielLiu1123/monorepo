#!/usr/bin/env bash

install() {
    cd $PROJECT_DIR && go mod download
    go work sync
}

build() {
    cd $PROJECT_DIR && go build ./...
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
    print_info "No build artifacts to clean for generated code"
}

run() {
    print_info "No thing to run for generated code"
}
