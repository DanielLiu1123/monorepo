#!/usr/bin/env bash

# Lint Go project
lint_go() {
    local project_path="$1"

    execute_command "(golangci-lint run ./$project_path/...)" || return 1
}

# Lint Gradle project
lint_gradle() {
    local project_path="$1"

    execute_command "(./gradlew spotlessCheck spotbugsMain --project-dir $project_path -S)" || return 1
}

# Lint npm project
lint_npm() {
    local project_path="$1"

    # Check if lint script exists
    if grep -q '"lint"[[:space:]]*:' "$project_path/package.json" 2>/dev/null; then
        execute_command "(cd $project_path && npm run lint)" || return 1
    else
        print_warning "No lint script found in package.json, skipping linting"
    fi
}

cmd_lint() {
    local project_path
    local project_type
    project_path=$(normalize_path "$1")
    project_type=$(detect_project_type "$project_path")

    print_info "Linting $project_path (type: $project_type)..."

    case "$project_type" in
        go)
            lint_go "$project_path" || return 1
            ;;
        gradle)
            lint_gradle "$project_path" || return 1
            ;;
        npm)
            lint_npm "$project_path" || return 1
            ;;
        *)
            print_error "Unknown project type in $project_path"
            return 1
            ;;
    esac

    print_success "Linting completed successfully for $project_path"
}
