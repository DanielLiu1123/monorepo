#!/usr/bin/env bash

install() {
    execute_cmd "./gradlew compileJava --project-dir $PROJECT_REL_DIR"
}

build() {
    execute_cmd "./gradlew bootJar --project-dir $PROJECT_REL_DIR"
}

test() {
    execute_cmd "./gradlew test --project-dir $PROJECT_REL_DIR"
}

lint() {
    execute_cmd "./gradlew compileJava compileTestJava spotlessCheck --project-dir $PROJECT_REL_DIR"
}

fmt() {
    execute_cmd "./gradlew spotlessApply --project-dir $PROJECT_REL_DIR"
}

clean() {
    execute_cmd "./gradlew clean --project-dir $PROJECT_REL_DIR"
}

run() {
    execute_cmd "./gradlew bootRun --project-dir $PROJECT_REL_DIR"
}
