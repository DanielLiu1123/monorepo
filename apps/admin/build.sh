#!/usr/bin/env bash

install() {
    execute_cmd "cd $PROJECT_DIR && npm install"
}

build() {
    if grep -q '"build"[[:space:]]*:' "$PROJECT_DIR/package.json" 2>/dev/null; then
        execute_cmd "cd $PROJECT_DIR && npm run build"
    else
        print_warning "No build script found in package.json, skipping build"
    fi
}

test() {
    if grep -q '"test"[[:space:]]*:' "$PROJECT_DIR/package.json" 2>/dev/null; then
        execute_cmd "cd $PROJECT_DIR && npm test"
    else
        print_warning "No test script found in package.json, skipping tests"
    fi
}

lint() {
    if grep -q '"lint"[[:space:]]*:' "$PROJECT_DIR/package.json" 2>/dev/null; then
        execute_cmd "cd $PROJECT_DIR && npm run lint"
    else
        print_warning "No lint script found in package.json, skipping linting"
    fi
}

fmt() {
    if command -v prettier &> /dev/null; then
        execute_cmd "cd $PROJECT_DIR && prettier --write ."
    elif [ -f "$PROJECT_DIR/node_modules/.bin/prettier" ]; then
        execute_cmd "cd $PROJECT_DIR && npx prettier --write ."
    else
        print_warning "Prettier not found, skipping formatting"
    fi
}

clean() {
    execute_cmd "rm -rf $PROJECT_DIR/build/ $PROJECT_DIR/.next/ $PROJECT_DIR/dist/"
}

run() {
    if grep -q '"dev"[[:space:]]*:' "$PROJECT_DIR/package.json" 2>/dev/null; then
        execute_cmd "cd $PROJECT_DIR && npm run dev"
    elif grep -q '"start"[[:space:]]*:' "$PROJECT_DIR/package.json" 2>/dev/null; then
        execute_cmd "cd $PROJECT_DIR && npm run start"
    else
        print_error "No dev or start script found in package.json"
        return 1
    fi
}
