#!/usr/bin/env bash

# Monorepo Build System
# Main entry point and common utilities

set -e

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

# ============================================================================
# Common Functions and Utilities
# ============================================================================

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_info() {
    echo -e "${BLUE}$1${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}" >&2
}

print_warning() {
    echo -e "${YELLOW}$1${NC}"
}

execute_command() {
    echo -e "${BLUE}→ $*${NC}"
    eval "$@"
}

# Normalize path: remove leading ./ and trailing /
normalize_path() {
    local path="$1"
    path="${path#./}"
    path="${path%/}"
    echo "$path"
}

detect_project_type() {
    local project_path="$1"

    if [ -f "$project_path/go.mod" ]; then
        echo "go"
    elif [ -f "$project_path/build.gradle" ] || [ -f "$project_path/build.gradle.kts" ]; then
        echo "gradle"
    elif [ -f "$project_path/package.json" ]; then
        echo "npm"
    else
        echo "unknown"
    fi
}

detect_project_category() {
    local project_path="$1"

    # Gradle projects: check for Spring Boot plugin
    if [ -f "$project_path/build.gradle" ] || [ -f "$project_path/build.gradle.kts" ]; then
        local gradle_file
        [ -f "$project_path/build.gradle" ] && gradle_file="$project_path/build.gradle" || gradle_file="$project_path/build.gradle.kts"

        # Check for org.springframework.boot plugin (apply, id, or plugin DSL)
        if grep -qE '.*id .*org\.springframework\.boot.*' "$gradle_file" 2>/dev/null; then
            echo "app"
            return
        else
            echo "lib"
            return
        fi
    fi

    # Go projects: check for cmd directory
    if [ -f "$project_path/go.mod" ]; then
        if [ -d "$project_path/cmd" ]; then
            echo "app"
            return
        else
            echo "lib"
            return
        fi
    fi

    # npm projects: use best practices to determine category
    if [ -f "$project_path/package.json" ]; then
        local pkg_json="$project_path/package.json"

        # Check if it's a library (has main, exports, or types field)
        if grep -qE '"(main|exports|types)"[[:space:]]*:' "$pkg_json" 2>/dev/null; then
            echo "lib"
            return
        fi

        # Check if it's an app (has private: true or start/dev scripts without main/exports)
        if grep -q '"private"[[:space:]]*:[[:space:]]*true' "$pkg_json" 2>/dev/null; then
            echo "app"
            return
        fi

        # Check for app-like scripts (start, dev, serve)
        if grep -qE '"(start|dev|serve)"[[:space:]]*:' "$pkg_json" 2>/dev/null; then
            echo "app"
            return
        fi

        # Default to lib for npm if uncertain
        echo "lib"
        return
    fi

    # Default to lib if uncertain
    echo "lib"
}

find_projects() {
    local search_path="$1"

    if [ ! -d "$search_path" ]; then
        print_error "path not exists or not a directory: $search_path"
        return 1
    fi

    find "$search_path" -type f \( \
        -name "go.mod" \
        -o -name "build.gradle" \
        -o -name "build.gradle.kts" \
        -o -name "package.json" \
    \) -print0 2>/dev/null \
    | xargs -0 -n1 dirname \
    | sort -u
}

is_project_dir() {
    local path="$1"
    local project_type
    project_type=$(detect_project_type "$path")
    [ "$project_type" != "unknown" ]
}

validate_project() {
    local project_path="$1"

    if [ -z "$project_path" ]; then
        print_error "Error: project path is required"
        echo "Usage: $0 <command> <project-path>"
        exit 1
    fi

    if [ ! -d "$project_path" ]; then
        print_error "Error: Project directory '$project_path' does not exist"
        exit 1
    fi
}

# Execute command for single project or all projects in a directory
execute_for_projects() {
    local command="$1"
    local path="$2"

    print_info "Scanning for projects in $path..."
    local projects
    projects=$(find_projects "$path")

    if [ -z "$projects" ]; then
        print_warning "No projects found in $path"
        return 0
    fi

    local total=0
    local success=0
    local failed=0

    while IFS= read -r project; do
        ((total++))
        echo ""
        print_info "[$total] Processing: $project"
        if "cmd_${command}" "$project"; then
            ((success++))
        else
            ((failed++))
        fi
    done <<< "$projects"

    echo ""
    echo "================================"
    print_info "Summary: $total project(s) processed"
    print_success "$success succeeded"
    if [ $failed -gt 0 ]; then
        print_error "$failed failed"
        return 1
    fi
}

# ============================================================================
# Load Command Modules
# ============================================================================

source "$SCRIPT_DIR/commands/build.sh"
source "$SCRIPT_DIR/commands/clean.sh"
source "$SCRIPT_DIR/commands/format.sh"
source "$SCRIPT_DIR/commands/gen-proto.sh"
source "$SCRIPT_DIR/commands/init.sh"
source "$SCRIPT_DIR/commands/install.sh"
source "$SCRIPT_DIR/commands/lint.sh"
source "$SCRIPT_DIR/commands/list-projects.sh"
source "$SCRIPT_DIR/commands/run.sh"
source "$SCRIPT_DIR/commands/test.sh"

# ============================================================================
# Main Functions
# ============================================================================

show_usage() {
    local script_name="./scripts/build/main.sh"
    echo "Usage: $script_name <command> [path]"
    echo ""
    echo "Commands:"
    echo "  init              - Initialize development environment"
    echo "  gen-proto [path]  - Generate code from proto files"
    echo "  list-projects     - List all projects in monorepo (or in specified path)"
    echo "  clean <path>      - Clean build artifacts"
    echo "  install <path>    - Install dependencies"
    echo "  build <path>      - Build project(s)"
    echo "  test <path>       - Run tests"
    echo "  run <path>        - Run project (single project only)"
    echo "  lint <path>       - Run linter"
    echo "  format <path>     - Format code (go fmt, spotlessApply, prettier)"
    echo ""
    echo "Examples:"
    echo "  $script_name init"
    echo "  $script_name gen-proto                      # Auto-detect changed proto files"
    echo "  $script_name gen-proto user                 # Generate code for user proto files"
    echo "  $script_name gen-proto user/v1              # Generate code for specific version"
    echo "  $script_name list-projects                  # List all projects"
    echo "  $script_name list-projects services         # List projects in services/"
    echo "  $script_name build services/user-service    # Build single project"
    echo "  $script_name build services                 # Build all projects in services/"
    echo "  $script_name build .                        # Build all projects in monorepo"
}

main() {
    local command="$1"
    local path="$2"

    if [ -z "$command" ]; then
        show_usage
        exit 1
    fi

    case "$command" in
        init)
            cmd_init
            ;;
        gen-proto)
            cmd_gen_proto "$path"
            ;;
        list-projects)
            cmd_list_projects "${path:-.}"
            ;;
        clean|install|build|test|lint|format)
            validate_project "$path"

            # Check if path is a project directory or a parent directory
            if is_project_dir "$path"; then
                # Single project: execute command directly
                cmd_"${command}" "$path"
            else
                # Parent directory: find and execute for all projects
                execute_for_projects "${command}" "$path"
            fi
            ;;
        run)
            # Run command only works for single projects
            validate_project "$path"
            if ! is_project_dir "$path"; then
                print_error "The 'run' command only works for single projects, not directories"
                print_info "Please specify a project path"
                exit 1
            fi
            cmd_run "$path"
            ;;
        help|--help|-h)
            show_usage
            ;;
        *)
            print_error "Unknown command: $command"
            show_usage
            exit 1
            ;;
    esac
}

main "$@"
