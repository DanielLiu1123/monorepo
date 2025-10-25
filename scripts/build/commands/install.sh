#!/usr/bin/env bash

# Install Go dependencies
install_go() {
    local project_path="$1"

    execute_command "(cd $project_path && go mod download)" || return 1
    execute_command "(go work sync)" || return 1
}

# Install Gradle dependencies
install_gradle() {
    local project_path="$1"

    execute_command "(./gradlew compileJava --project-dir $project_path -S)" || return 1
}

# Install npm dependencies
install_npm() {
    local project_path="$1"

    execute_command "(cd $project_path && npm install)" || return 1
}

cmd_install() {
    local project_path="$1"
    local project_type
    project_type=$(detect_project_type "$project_path")

    print_info "Installing dependencies for $project_path (type: $project_type)..."

    case "$project_type" in
        go)
            install_go "$project_path" || return 1
            ;;
        gradle)
            install_gradle "$project_path" || return 1
            ;;
        npm)
            install_npm "$project_path" || return 1
            ;;
        *)
            print_error "Unknown project type in $project_path"
            return 1
            ;;
    esac

    print_success "Dependencies installed successfully for $project_path"
}
