#!/usr/bin/env bash

install() {
    execute_command "cd $PROJECT_DIR && go mod download" || return 1
    execute_command "go work sync" || return 1
}

build() {
    # Build lib: compile check
    local rel_path="${PROJECT_DIR#$ROOT_DIR/}"
    execute_command "go build ./$rel_path/..." || return 1
}

test() {
    # Run tests only if test files exist
    if find "$PROJECT_DIR" -name "*_test.go" -type f | grep -q .; then
        local rel_path="${PROJECT_DIR#$ROOT_DIR/}"
        execute_command "go test -v ./$rel_path/..." || return 1
    else
        print_info "No test files found, skipping tests"
    fi
}

lint() {
    local rel_path="${PROJECT_DIR#$ROOT_DIR/}"
    execute_command "golangci-lint run ./$rel_path/..." || return 1
}

fmt() {
    execute_command "cd $PROJECT_DIR && go fmt ./... && goimports -w . && go mod tidy" || return 1
}

clean() {
    local rel_path="${PROJECT_DIR#$ROOT_DIR/}"
    execute_command "go clean ./$rel_path/... && rm -rf $PROJECT_DIR/build/" || return 1
}
