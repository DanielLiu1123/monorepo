#!/usr/bin/env bash

# Build Go project (app or lib)
build_go() {
    local project_path="$1"
    local project_category
    project_category=$(detect_project_category "$project_path")

    case "$project_category" in
        app)
            # Build app: compile to executable binary

            # Find all cmd directories
            if [ -d "$project_path/cmd" ]; then
                for cmd_dir in "$project_path"/cmd/*; do
                    if [ -d "$cmd_dir" ]; then
                        local app_name
                        app_name=$(basename "$cmd_dir")
                        execute_command "(go build -o $project_path/build/$app_name ./$project_path/cmd/$app_name/)" || return 1
                    fi
                done
            else
                print_error "No cmd directory found for Go app"
                return 1
            fi
            ;;
        lib)
            # Build lib: compile check and run tests
            execute_command "(go build ./$project_path/...)" || return 1

            # Run tests only if test files exist
            if find "$project_path" -name "*_test.go" -type f | grep -q .; then
                execute_command "(go test ./$project_path/... -v)" || return 1
            else
                print_info "No test files found, skipping tests"
            fi
            ;;
        *)
            print_error "Unknown project category: $project_category"
            return 1
            ;;
    esac
}

# Build Gradle project (app or lib)
build_gradle() {
    local project_path="$1"
    local project_category

    project_category=$(detect_project_category "$project_path")

    case "$project_category" in
        app)
            # Build app: create executable jar
            execute_command "(./gradlew bootJar --project-dir $project_path -S)" || return 1
            ;;
        lib)
            # Build lib: create library jar
            execute_command "(./gradlew jar --project-dir $project_path -S)" || return 1
            ;;
        *)
            print_error "Unknown project category: $project_category"
            return 1
            ;;
    esac
}

# Build npm project (app or lib)
build_npm() {
    local project_path="$1"
    local project_category

    project_category=$(detect_project_category "$project_path")

    case "$project_category" in
        app)
            # Build app: build for production
            # Check if build script exists
            if grep -q '"build"[[:space:]]*:' "$project_path/package.json" 2>/dev/null; then
                execute_command "(cd $project_path && npm run build)" || return 1
            else
                print_warning "No build script found in package.json, skipping build"
            fi
            ;;
        lib)
            # Build lib: build library
            # Check for lib-specific build script first, fallback to regular build
            if grep -q '"build:lib"[[:space:]]*:' "$project_path/package.json" 2>/dev/null; then
                execute_command "(cd $project_path && npm run build:lib)" || return 1
            elif grep -q '"build"[[:space:]]*:' "$project_path/package.json" 2>/dev/null; then
                execute_command "(cd $project_path && npm run build)" || return 1
            else
                # If no build script, try to compile TypeScript directly
                if [ -f "$project_path/tsconfig.json" ]; then
                    execute_command "(cd $project_path && npx tsc)" || return 1
                else
                    print_warning "No build configuration found, skipping build"
                fi
            fi
            ;;
        *)
            print_error "Unknown project category: $project_category"
            return 1
            ;;
    esac
}

cmd_build() {
    local project_path
    local project_type
    project_path=$(normalize_path "$1")
    project_type=$(detect_project_type "$project_path")

    print_info "Building $project_path (type: $project_type)..."

    case "$project_type" in
        go)
            build_go "$project_path" || return 1
            ;;
        gradle)
            build_gradle "$project_path" || return 1
            ;;
        npm)
            build_npm "$project_path" || return 1
            ;;
        *)
            print_error "Unknown project type in $project_path"
            return 1
            ;;
    esac

    print_success "$project_path built successfully"
}
