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
    printf "${BLUE}%-60s${NC}\n" "PROJECT PATH"
    printf "%-60s\n" "$(printf '=%.0s' {1..60})"

    # Print each project
    local count=0
    while IFS= read -r project; do
        printf "%-60s\n" "$project"
        ((count++))
    done <<< "$projects"

    echo ""
    print_success "Found $count project(s) with build.sh"
}
