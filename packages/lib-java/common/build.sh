#!/usr/bin/env bash

install() {
    execute_command "./gradlew compileJava --project-dir $PROJECT_DIR -S" || return 1
}

build() {
    # Build lib: create library jar
    execute_command "./gradlew jar --project-dir $PROJECT_DIR -S" || return 1
}

test() {
    execute_command "./gradlew test --project-dir $PROJECT_DIR -S" || return 1
}

lint() {
    execute_command "./gradlew spotlessCheck spotbugsMain --project-dir $PROJECT_DIR -S" || return 1
}

fmt() {
    execute_command "./gradlew spotlessApply --project-dir $PROJECT_DIR -S" || return 1
}

clean() {
    execute_command "./gradlew clean --project-dir $PROJECT_DIR -S" || return 1
}
