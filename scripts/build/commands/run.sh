#!/usr/bin/env bash

# Run Go app
run_go() {
    local project_path="$1"

    execute_command "(go run ./$project_path/cmd/app/)"
}

# Run Gradle app
run_gradle() {
    local project_path="$1"

    execute_command "(./gradlew bootRun --project-dir $project_path -S)"
}

# Run npm app
run_npm() {
    local project_path="$1"

    # Check if dev script exists, fallback to start
    if grep -q '"dev"[[:space:]]*:' "$project_path/package.json" 2>/dev/null; then
        execute_command "(cd $project_path && npm run dev)"
    elif grep -q '"start"[[:space:]]*:' "$project_path/package.json" 2>/dev/null; then
        execute_command "(cd $project_path && npm run start)"
    else
        print_error "No dev or start script found in package.json"
        return 1
    fi
}

cmd_run() {
    local project_path
    local project_type
    local project_category
    project_path=$(normalize_path "$1")
    project_type=$(detect_project_type "$project_path")
    project_category=$(detect_project_category "$project_path")

    case "$project_category" in
        app)
            # Valid category for run command
            ;;
        lib)
            print_error "Run command is only supported for app projects. '$project_path' is a '$project_category'."
            return 1
            ;;
        *)
            print_error "Unknown project category: $project_category"
            return 1
            ;;
    esac

    print_info "Running $project_path (type: $project_type)..."

    case "$project_type" in
        go)
            run_go "$project_path"
            ;;
        gradle)
            run_gradle "$project_path"
            ;;
        npm)
            run_npm "$project_path"
            ;;
        *)
            print_error "Unknown project type in $project_path"
            return 1
            ;;
    esac
}
