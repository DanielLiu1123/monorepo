#!/usr/bin/env bash

install() {
    cd $PROJECT_DIR && go mod download
    go work sync
}

build() {
    mkdir -p $PROJECT_DIR/build
    for cmd_dir in $PROJECT_DIR/cmd/*; do
        if [ -d "$cmd_dir" ]; then
            local app_name=$(basename "$cmd_dir")
            local rel_path="${PROJECT_DIR#$ROOT_DIR/}"
            go build -o $PROJECT_DIR/build/$app_name ./$rel_path/cmd/$app_name/
        fi
    done
}

test() {
    if find "$PROJECT_DIR" -name "*_test.go" -type f | grep -q .; then
        local rel_path="${PROJECT_DIR#$ROOT_DIR/}"
        go test -v ./$rel_path/...
    else
        print_info "No test files found, skipping tests"
    fi
}

lint() {
    local rel_path="${PROJECT_DIR#$ROOT_DIR/}"
    golangci-lint run ./$rel_path/...
}

fmt() {
    cd $PROJECT_DIR && go fmt ./... && goimports -w . && go mod tidy
}

clean() {
    local rel_path="${PROJECT_DIR#$ROOT_DIR/}"
    go clean ./$rel_path/...
    rm -rf $PROJECT_DIR/build/
}

run() {
    local rel_path="${PROJECT_DIR#$ROOT_DIR/}"
    go run ./$rel_path/cmd/app/
}
