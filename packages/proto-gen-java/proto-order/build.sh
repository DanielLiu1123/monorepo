#!/usr/bin/env bash

install() {
    ./gradlew compileJava --project-dir $PROJECT_DIR
}

build() {
    ./gradlew jar --project-dir $PROJECT_DIR
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
    ./gradlew clean --project-dir $PROJECT_DIR
}

run() {
    print_info "No thing to run for generated code"
}
