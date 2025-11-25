#!/usr/bin/env bash

install() {
    execute_cmd "./gradlew compileJava --project-dir $PROJECT_DIR"
}

build() {
    execute_cmd "./gradlew jar --project-dir $PROJECT_DIR"
}

test() {
    execute_cmd "./gradlew test --project-dir $PROJECT_DIR"
}

lint() {
    execute_cmd "./gradlew spotlessCheck spotbugsMain --project-dir $PROJECT_DIR"
}

fmt() {
    execute_cmd "./gradlew spotlessApply --project-dir $PROJECT_DIR"
}

clean() {
    execute_cmd "./gradlew clean --project-dir $PROJECT_DIR"
}

run() {
    print_info "Nothing to run"
}
