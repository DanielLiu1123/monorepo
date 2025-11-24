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

execute_cmd() {
    echo -e "${BLUE}→ $*${NC}"
    ( eval "$@" )
}

# Execute project command
execute_project_cmd() {
    local command="$1"
    local project_path
    project_path=$(normalize_path "$2")

    # Set environment variables for the project
    export ROOT_DIR
    export PROJECT_DIR="$ROOT_DIR/$project_path"

    # Check if build.sh exists
    if [ ! -f "$PROJECT_DIR/build.sh" ]; then
        print_error "No build.sh found in $project_path"
        return 1
    fi

    # Load project's build.sh
    # shellcheck disable=SC1090
    source "$PROJECT_DIR/build.sh"

    # Check if command function exists
    if ! declare -f "$command" > /dev/null 2>&1; then
        print_error "No '$command' function found in $project_path/build.sh"
        return 1
    fi

    # Execute command
    $command || return 1
}

# Normalize path: remove leading ./ and trailing /
normalize_path() {
    local path="$1"
    path="${path#./}"
    path="${path%/}"
    echo "$path"
}

find_projects() {
    local search_path="$1"

    if [ ! -d "$search_path" ]; then
        print_error "path not exists or not a directory: $search_path"
        return 1
    fi

    find "$search_path" -type f -name "build.sh" -print0 2>/dev/null \
    | xargs -0 -n1 dirname \
    | sort -u
}

is_project_dir() {
    local path="$1"
    [ -f "$path/build.sh" ]
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

    print_info "Scanning for projects in $path"
    local projects
    projects=$(find_projects "$path")

    if [ -z "$projects" ]; then
        print_warning "No projects found in $path"
        return 0
    fi

    local total=0
    local success=0
    local failed=0
    local failed_projects=()

    while IFS= read -r project; do
        project="$(normalize_path "$project")"
        ((total++))
        echo ""
        print_info "[$total] Processing: $project"
        if execute_project_cmd "$command" "$project"; then
            ((success++))
        else
            ((failed++))
            failed_projects+=("$project")
        fi
    done <<< "$projects"

    echo ""
    echo "================================"
    print_info "Summary: $total project(s) processed"
    print_success "$success succeeded"
    if [ $failed -gt 0 ]; then
        print_error "$failed failed"
        echo ""
        print_error "Failed projects:"
        for project in "${failed_projects[@]}"; do
            echo "  - $project"
        done
        return 1
    fi
}

# ============================================================================
# Load Command Modules
# ============================================================================

source "$SCRIPT_DIR/commands/gen-proto.sh"
source "$SCRIPT_DIR/commands/init.sh"
source "$SCRIPT_DIR/commands/list-projects.sh"

# ============================================================================
# Main Functions
# ============================================================================

show_usage() {
    local script_name="./scripts/build/main.sh"
    echo "Usage: $script_name <command> [path]"
    echo ""
    echo "Commands:"
    echo "  init                - Initialize development environment"
    echo "  gen-proto [path]    - Generate code from proto files"
    echo "  list-projects [path] - List all projects in monorepo (or in specified path)"
    echo "  clean [path]        - Clean build artifacts (defaults to current directory)"
    echo "  install [path]      - Install dependencies (defaults to current directory)"
    echo "  build [path]        - Build project(s) (defaults to current directory)"
    echo "  test [path]         - Run tests (defaults to current directory)"
    echo "  run <path>          - Run project (single project only, path required)"
    echo "  lint [path]         - Run linter (defaults to current directory)"
    echo "  fmt [path]          - Format code (defaults to current directory)"
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
    local path
    path=$(normalize_path "$2")

    if [ -z "$command" ]; then
        show_usage
        exit 0
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
        clean|install|build|test|lint|fmt)
            # Default to current directory if no path specified
            path="${path:-.}"
            validate_project "$path"

            # Check if path is a project directory or a parent directory
            if is_project_dir "$path"; then
                # Single project: execute command with custom support
                execute_project_cmd "${command}" "$path"
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
            execute_project_cmd "${command}" "$path"
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
