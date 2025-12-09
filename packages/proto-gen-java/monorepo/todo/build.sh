#!/usr/bin/env bash

install() {
    execute_cmd "./gradlew compileJava --project-dir $PROJECT_DIR"
}

build() {
    execute_cmd "./gradlew jar --project-dir $PROJECT_DIR"
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
    execute_cmd "./gradlew clean --project-dir $PROJECT_DIR"
}

run() {
    print_info "Nothing to run for generated code"
}
