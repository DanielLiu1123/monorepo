#!/usr/bin/env bash

install() {
    execute_cmd "cd $PROJECT_DIR && go mod download"
    execute_cmd "go work sync"
}

build() {
    execute_cmd "mkdir -p $PROJECT_DIR/build"
    for cmd_dir in $PROJECT_DIR/cmd/*; do
        if [ -d "$cmd_dir" ]; then
            local app_name=$(basename "$cmd_dir")
            local rel_path="${PROJECT_DIR#$ROOT_DIR/}"
            execute_cmd "go build -o $PROJECT_DIR/build/$app_name ./$rel_path/cmd/$app_name/"
        fi
    done
}

test() {
    if find "$PROJECT_DIR" -name "*_test.go" -type f | grep -q .; then
        local rel_path="${PROJECT_DIR#$ROOT_DIR/}"
        execute_cmd "go test -v ./$rel_path/..."
    else
        print_info "No test files found, skipping tests"
    fi
}

lint() {
    local rel_path="${PROJECT_DIR#$ROOT_DIR/}"
    execute_cmd "golangci-lint run ./$rel_path/..."
}

fmt() {
    execute_cmd "cd $PROJECT_DIR && go fmt ./... && goimports -w . && go mod tidy"
}

clean() {
    local rel_path="${PROJECT_DIR#$ROOT_DIR/}"
    execute_cmd "go clean ./$rel_path/..."
    execute_cmd "rm -rf $PROJECT_DIR/build/"
}

run() {
    local rel_path="${PROJECT_DIR#$ROOT_DIR/}"
    execute_cmd "go run ./$rel_path/cmd/app/"
}
