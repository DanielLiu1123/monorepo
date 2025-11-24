#!/usr/bin/env bash

install() {
    execute_command "cd $PROJECT_DIR && npm install" || return 1
}

build() {
    # Build app: build for production
    if grep -q '"build"[[:space:]]*:' "$PROJECT_DIR/package.json" 2>/dev/null; then
        execute_command "cd $PROJECT_DIR && npm run build" || return 1
    else
        print_warning "No build script found in package.json, skipping build"
    fi
}

test() {
    # Check if test script exists
    if grep -q '"test"[[:space:]]*:' "$PROJECT_DIR/package.json" 2>/dev/null; then
        execute_command "cd $PROJECT_DIR && npm test" || return 1
    else
        print_warning "No test script found in package.json, skipping tests"
    fi
}

lint() {
    # Check if lint script exists
    if grep -q '"lint"[[:space:]]*:' "$PROJECT_DIR/package.json" 2>/dev/null; then
        execute_command "cd $PROJECT_DIR && npm run lint" || return 1
    else
        print_warning "No lint script found in package.json, skipping linting"
    fi
}

fmt() {
    # Check if prettier is available
    if command -v prettier &> /dev/null; then
        execute_command "cd $PROJECT_DIR && prettier --write ." || return 1
    elif [ -f "$PROJECT_DIR/node_modules/.bin/prettier" ]; then
        execute_command "cd $PROJECT_DIR && npx prettier --write ." || return 1
    else
        print_warning "Prettier not found, skipping formatting"
    fi
}

clean() {
    execute_command "cd $PROJECT_DIR && rm -rf build/ .next/ dist/" || return 1
}

run() {
    # Check if dev script exists, fallback to start
    if grep -q '"dev"[[:space:]]*:' "$PROJECT_DIR/package.json" 2>/dev/null; then
        execute_command "cd $PROJECT_DIR && npm run dev"
    elif grep -q '"start"[[:space:]]*:' "$PROJECT_DIR/package.json" 2>/dev/null; then
        execute_command "cd $PROJECT_DIR && npm run start"
    else
        print_error "No dev or start script found in package.json"
        return 1
    fi
}
