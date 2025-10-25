#!/usr/bin/env bash

# Test Go project
test_go() {
    local project_path="$1"

    # Run tests only if test files exist
    if find "$project_path" -name "*_test.go" -type f | grep -q .; then
        execute_command "(go test -v ./$project_path/...)" || return 1
    else
        print_info "No test files found, skipping tests"
    fi
}

# Test Gradle project
test_gradle() {
    local project_path="$1"

    execute_command "(./gradlew test --project-dir $project_path -S)" || return 1
}

# Test npm project
test_npm() {
    local project_path="$1"

    # Check if test script exists
    if grep -q '"test"[[:space:]]*:' "$project_path/package.json" 2>/dev/null; then
        execute_command "(cd $project_path && npm test)" || return 1
    else
        print_warning "No test script found in package.json, skipping tests"
    fi
}

cmd_test() {
    local project_path
    local project_type
    project_path=$(normalize_path "$1")
    project_type=$(detect_project_type "$project_path")

    print_info "Testing $project_path (type: $project_type)..."

    case "$project_type" in
        go)
            test_go "$project_path" || return 1
            ;;
        gradle)
            test_gradle "$project_path" || return 1
            ;;
        npm)
            test_npm "$project_path" || return 1
            ;;
        *)
            print_error "Unknown project type in $project_path"
            return 1
            ;;
    esac

    print_success "Tests passed for $project_path"
}
