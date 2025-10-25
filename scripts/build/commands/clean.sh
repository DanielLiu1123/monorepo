#!/usr/bin/env bash

# Clean Go project
clean_go() {
    local project_path="$1"

    execute_command "(go clean ./$project_path/... && rm -rf $project_path/build/)" || return 1
}

# Clean Gradle project
clean_gradle() {
    local project_path="$1"

    execute_command "(./gradlew clean --project-dir $project_path -S)" || return 1
}

# Clean npm project
clean_npm() {
    local project_path="$1"

    execute_command "(cd $project_path && rm -rf build/ .next/)" || return 1
}

cmd_clean() {
    local project_path
    local project_type
    project_path=$(normalize_path "$1")
    project_type=$(detect_project_type "$project_path")

    print_info "Cleaning $project_path (type: $project_type)..."

    case "$project_type" in
        go)
            clean_go "$project_path" || return 1
            ;;
        gradle)
            clean_gradle "$project_path" || return 1
            ;;
        npm)
            clean_npm "$project_path" || return 1
            ;;
        *)
            print_error "Unknown project type in $project_path"
            return 1
            ;;
    esac

    print_success "$project_path cleaned successfully"
}
