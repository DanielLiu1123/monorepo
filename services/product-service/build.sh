#!/usr/bin/env bash

install() {
    ./gradlew compileJava --project-dir $PROJECT_DIR
}

build() {
    ./gradlew bootJar --project-dir $PROJECT_DIR
}

test() {
    ./gradlew test --project-dir $PROJECT_DIR
}

lint() {
    ./gradlew spotlessCheck spotbugsMain --project-dir $PROJECT_DIR
}

fmt() {
    ./gradlew spotlessApply --project-dir $PROJECT_DIR
}

clean() {
    ./gradlew clean --project-dir $PROJECT_DIR
}

run() {
    ./gradlew bootRun --project-dir $PROJECT_DIR
}
