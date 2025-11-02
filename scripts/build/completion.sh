#!/usr/bin/env bash

# Bash Completion Script
#
# Installation:
#   source ./scripts/build/completion.sh


# ============================================================================
# Helper Functions
# ============================================================================

_mono_get_root_dir() {
    # Try to determine the monorepo root directory
    if [ -n "$MONO_ROOT" ]; then
        echo "$MONO_ROOT"
        return
    fi

    # If BASH_SOURCE is available, use it to find the script directory
    if [ -n "${BASH_SOURCE[0]}" ]; then
        local script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
        echo "$(cd "$script_dir/../.." && pwd)"
        return
    fi

    # Fallback to current directory
    pwd
}

_mono_find_projects() {
    local root_dir="$1"

    # Find all projects directly
    find "$root_dir" -type f \( \
        -name "go.mod" \
        -o -name "build.gradle" \
        -o -name "build.gradle.kts" \
        -o -name "package.json" \
    \) 2>/dev/null \
    | xargs -n1 dirname 2>/dev/null \
    | sed "s|^$root_dir/||" \
    | grep -v "node_modules" \
    | grep -v ".gradle" \
    | sort -u
}

_mono_find_directories() {
    local root_dir="$1"
    local cur="$2"

    # Get base directory from current input
    local base_dir="$root_dir"
    local search_pattern="$cur"

    if [[ "$cur" == */* ]]; then
        local parent_path="${cur%/*}"
        base_dir="$root_dir/$parent_path"
        search_pattern="${cur##*/}"
    fi

    # Find directories matching the pattern
    if [ -d "$base_dir" ]; then
        find "$base_dir" -maxdepth 1 -type d -name "${search_pattern}*" 2>/dev/null \
        | sed "s|^$root_dir/||" \
        | grep -v "^\\.git" \
        | grep -v "^\\.idea" \
        | grep -v "^node_modules"
    fi
}

# ============================================================================
# Main Completion Function
# ============================================================================

_mono_completion() {
    local cur prev words cword
    COMPREPLY=()

    # Get current word and previous word
    cur="${COMP_WORDS[COMP_CWORD]}"
    prev="${COMP_WORDS[COMP_CWORD-1]}"
    cword=$COMP_CWORD

    # Available commands
    local commands="init gen-proto list-projects clean install build test run lint format help"

    # Get root directory
    local root_dir=$(_mono_get_root_dir)

    # First argument: complete command names
    if [ $cword -eq 1 ]; then
        COMPREPLY=( $(compgen -W "$commands" -- "$cur") )
        return 0
    fi

    # Second argument: complete paths based on command
    if [ $cword -eq 2 ]; then
        local command="${COMP_WORDS[1]}"

        case "$command" in
            init|help|--help|-h)
                # No path completion needed
                return 0
                ;;
            gen-proto)
                # Complete proto directory names (packages/proto/*)
                local proto_dirs=""
                if [ -d "$root_dir/packages/proto" ]; then
                    proto_dirs=$(find "$root_dir/packages/proto" -maxdepth 2 -type d 2>/dev/null \
                        | sed "s|^$root_dir/packages/proto/||" \
                        | grep -v "^$" \
                        | sort -u)
                fi
                COMPREPLY=( $(compgen -W "$proto_dirs" -- "$cur") )

                # Also add common shortcuts
                local shortcuts="user product order"
                COMPREPLY+=( $(compgen -W "$shortcuts" -- "$cur") )
                return 0
                ;;
            list-projects)
                # Complete with common directory paths
                local common_dirs="services packages ."
                COMPREPLY=( $(compgen -W "$common_dirs" -- "$cur") )

                # Add directory completion
                local dirs=$(_mono_find_directories "$root_dir" "$cur")
                if [ -n "$dirs" ]; then
                    COMPREPLY+=( $(compgen -W "$dirs" -- "$cur") )
                fi
                return 0
                ;;
            build|test|clean|install|lint|format)
                # Complete with project paths and directories
                local projects=$(_mono_find_projects "$root_dir")
                COMPREPLY=( $(compgen -W "$projects" -- "$cur") )

                # Add common directory paths
                local common_dirs="services packages ."
                COMPREPLY+=( $(compgen -W "$common_dirs" -- "$cur") )

                # Add directory completion for partial paths
                local dirs=$(_mono_find_directories "$root_dir" "$cur")
                if [ -n "$dirs" ]; then
                    COMPREPLY+=( $(compgen -W "$dirs" -- "$cur") )
                fi
                return 0
                ;;
            run)
                # Run only works with single projects (apps)
                # Find only app projects (those with cmd/ or main files)
                local projects=$(_mono_find_projects "$root_dir")
                COMPREPLY=( $(compgen -W "$projects" -- "$cur") )
                return 0
                ;;
            *)
                return 0
                ;;
        esac
    fi

    return 0
}

# ============================================================================
# Register Completion
# ============================================================================

# Complete for both 'mono' and './mono' commands
complete -F _mono_completion mono
complete -F _mono_completion ./mono

# ============================================================================
# Zsh Compatibility (optional)
# ============================================================================

if [ -n "$ZSH_VERSION" ]; then
    autoload -U +X compinit && compinit
    autoload -U +X bashcompinit && bashcompinit

    # Now the bash completion should work in zsh too
    complete -F _mono_completion mono
    complete -F _mono_completion ./mono
fi
