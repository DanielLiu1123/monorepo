#!/usr/bin/env bash

# List all projects in the monorepo
cmd_list_projects() {
    local search_path="${1:-.}"

    print_info "Scanning for projects in $search_path..."
    echo ""

    local projects
    projects=$(find_projects "$search_path")

    if [ -z "$projects" ]; then
        print_warning "No projects found in $search_path"
        return 0
    fi

    # Print header
    printf "${BLUE}%-50s %-15s${NC}\n" "PROJECT PATH" "BUILD TOOL"
    printf "%-50s %-15s\n" "$(printf '=%.0s' {1..50})" "$(printf '=%.0s' {1..15})"

    # Print each project
    local count=0
    while IFS= read -r project; do
        local project_type
        project_type=$(detect_project_type "$project")
        local tool_name=""

        case "$project_type" in
            go)
                tool_name="Go"
                ;;
            gradle)
                tool_name="Gradle"
                ;;
            npm)
                tool_name="Node"
                ;;
            *)
                tool_name="Unknown"
                ;;
        esac

        printf "%-50s " "$project"
        echo -e "$tool_name"
        ((count++))
    done <<< "$projects"

    echo ""
    print_success "Found $count project(s)"
}
