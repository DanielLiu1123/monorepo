#!/usr/bin/env bash

install() {
    cd $PROJECT_DIR && go mod download
    go work sync
}

build() {
    mkdir -p $PROJECT_DIR/build
    cd $PROJECT_DIR && go build -o ./build/app ./cmd/app/
}

test() {
    cd $PROJECT_DIR && go test ./... -v
}

lint() {
    cd $PROJECT_DIR && golangci-lint run ./...
}

fmt() {
    cd $PROJECT_DIR && go fmt ./... && goimports -w . && go mod tidy
    go work sync
}

clean() {
    rm -rf $PROJECT_DIR/build/
}

run() {
    cd $PROJECT_DIR && go run ./cmd/app/
}