#!/usr/bin/env bash

# Format Go project
format_go() {
    local project_path="$1"

    execute_command "(go fmt ./$project_path/...)" || return 1
}

# Format Gradle project
format_gradle() {
    local project_path="$1"

    execute_command "(./gradlew spotlessApply --project-dir $project_path -S)" || return 1
}

# Format npm project
format_npm() {
    local project_path="$1"

    # Check if prettier is available
    if command -v prettier &> /dev/null; then
        execute_command "(cd $project_path && prettier --write .)" || return 1
    elif [ -f "$project_path/node_modules/.bin/prettier" ]; then
        execute_command "(cd $project_path && npx prettier --write .)" || return 1
    else
        print_warning "Prettier not found, skipping formatting"
        print_info "You can install it with: npm install --save-dev prettier"
    fi
}

cmd_format() {
    local project_path
    local project_type
    project_path=$(normalize_path "$1")
    project_type=$(detect_project_type "$project_path")

    print_info "Formatting $project_path (type: $project_type)..."

    case "$project_type" in
        go)
            format_go "$project_path" || return 1
            ;;
        gradle)
            format_gradle "$project_path" || return 1
            ;;
        npm)
            format_npm "$project_path" || return 1
            ;;
        *)
            print_error "Unknown project type in $project_path"
            return 1
            ;;
    esac

    print_success "Formatting completed successfully for $project_path"
}
