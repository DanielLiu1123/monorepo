#!/usr/bin/env bash

install() {
    execute_cmd "cd $PROJECT_DIR && go mod download"
    execute_cmd "go work sync"
}

build() {
    execute_cmd "mkdir -p $PROJECT_DIR/build"
    execute_cmd "cd $PROJECT_DIR && go build -o ./build/app ./cmd/app/"
}

test() {
    execute_cmd "cd $PROJECT_DIR && go test ./... -v"
}

lint() {
    execute_cmd "cd $PROJECT_DIR && golangci-lint run ./..."
}

fmt() {
    execute_cmd "cd $PROJECT_DIR && go fmt ./... && goimports -w . && go mod tidy"
    execute_cmd "go work sync"
}

clean() {
    execute_cmd "rm -rf $PROJECT_DIR/build/"
}

run() {
    execute_cmd "cd $PROJECT_DIR && go run ./cmd/app/"
}