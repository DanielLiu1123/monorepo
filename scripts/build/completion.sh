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

_mono_find_proto_packages() {
    local root_dir="$1"
    local proto_dir="$root_dir/packages/proto"

    if [ ! -d "$proto_dir" ]; then
        return
    fi

    # Find all .proto files, pruning common heavy directories
    local proto_files=$(find "$proto_dir" \
        \( -name ".git" -o -name ".idea" -o -name "node_modules" -o -name "build" -o -name "dist" -o -name "target" -o -name ".gradle" \) -prune \
        -o -type f -name "*.proto" -print 2>/dev/null)
    
    if [ -z "$proto_files" ]; then
        return
    fi

    # Extract p0/p1, and sort -u once at the end
    {
        # Extract p0/p1
        echo "$proto_files" | sed "s|^$proto_dir/||" | rev | cut -d'/' -f3- | rev
    } | sort -u
}

_mono_find_projects() {
    local root_dir="$1"

    # Find all projects directly, pruning common heavy directories
    find "$root_dir" \
        \( -name ".git" -o -name ".idea" -o -name "node_modules" -o -name "build" -o -name "dist" -o -name "target" -o -name ".gradle" \) -prune \
        -o -type f -name "build.sh" -print 2>/dev/null \
    | sed "s|^$root_dir/||" \
    | xargs -n1 dirname 2>/dev/null \
    | sort -u
}

_mono_find_directories() {
    local root_dir="$1"
    local cur="$2"
    local filter_projects="$3"

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
        if [ "$filter_projects" = "true" ]; then
            # Optimization: Use _mono_find_projects to get all valid project paths
            # then extract the relevant directory level relative to the current input
            local projects=$(_mono_find_projects "$root_dir")
            local filtered_dirs=""
            
            # If cur is "services/", we want "services/order-service/"
            # If cur is "", we want "services/", "packages/"
            local prefix="$cur"
            
            while IFS= read -r project; do
                if [[ "$project" == "$prefix"* ]]; then
                    # Get the part after the prefix
                    local suffix="${project#$prefix}"
                    # Get the next directory level
                    local next_part="${suffix%%/*}"
                    if [ -n "$next_part" ]; then
                        filtered_dirs+="$prefix$next_part/ "
                    fi
                fi
            done <<< "$projects"
            echo "$filtered_dirs" | tr ' ' '\n' | sort -u
        else
            local dirs=$(find "$base_dir" -maxdepth 1 -type d -name "${search_pattern}*" 2>/dev/null \
                | sed "s|^$root_dir/||" \
                | grep -vE "/(\.git|\.idea|node_modules|build|dist|target|\.gradle)(/|$)" \
                | grep -vE "^(\.git|\.idea|node_modules|build|dist|target|\.gradle)(/|$)" \
                | sed 's|/*$|/|') # Ensure single trailing slash
            echo "$dirs"
        fi
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
    local commands="init gen-proto list-projects clean install build test run lint fmt help"

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
                # Complete proto package names and version paths
                local proto_paths=$(_mono_find_proto_packages "$root_dir" | sort -u)
                COMPREPLY=( $(compgen -W "$proto_paths" -- "$cur") )
                return 0
                ;;
            list-projects)
                # Add common directory paths (with trailing slash)
                local common_dirs="services/ packages/ ./"
                COMPREPLY=( $(compgen -W "$common_dirs" -- "$cur") )

                # Add directory completion
                local dirs=$(_mono_find_directories "$root_dir" "$cur" "true")
                if [ -n "$dirs" ]; then
                    COMPREPLY+=( $(compgen -W "$dirs" -- "$cur") )
                fi

                # Filter out duplicates
                COMPREPLY=( $(printf "%s\n" "${COMPREPLY[@]}" | sort -u) )

                # Special handling for bash to not add space after directory
                if type compopt >/dev/null 2>&1; then
                    compopt -o nospace
                fi
                return 0
                ;;
            build|test|clean|install|lint|fmt)
                # Complete with project paths
                local projects=$(_mono_find_projects "$root_dir")
                COMPREPLY=( $(compgen -W "$projects" -- "$cur") )

                # Add common directory paths (with trailing slash for better experience)
                local common_dirs="services/ packages/ ./"
                COMPREPLY+=( $(compgen -W "$common_dirs" -- "$cur") )

                # Add directory completion for partial paths
                local dirs=$(_mono_find_directories "$root_dir" "$cur" "true")
                if [ -n "$dirs" ]; then
                    COMPREPLY+=( $(compgen -W "$dirs" -- "$cur") )
                fi
                
                # Filter out duplicates and handle the trailing slash properly for Bash
                COMPREPLY=( $(printf "%s\n" "${COMPREPLY[@]}" | sort -u) )
                
                # Special handling for bash to not add space after directory
                if type compopt >/dev/null 2>&1; then
                    compopt -o nospace
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
