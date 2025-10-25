.PHONY: help init gen-proto clean install build test run lint format docker-build docker-push version install-tools list-projects

# Colors for output
RED := \033[0;31m
GREEN := \033[0;32m
YELLOW := \033[0;33m
BLUE := \033[0;34m
NC := \033[0m # No Color

# Paths
ROOT_DIR := $(shell pwd)
PROTO_DIR := $(ROOT_DIR)/packages/proto
BUILD_SCRIPT := $(ROOT_DIR)/scripts/build/main.sh

# Define commands that require a project path (can be single project or directory)
PROJECT_COMMANDS := build test run lint format clean install docker-build list-projects gen-proto

# Extract project path from arguments (anything that's not a known command)
ifeq ($(filter $(firstword $(MAKECMDGOALS)),$(PROJECT_COMMANDS)),$(firstword $(MAKECMDGOALS)))
    # If first arg is a project command, second arg is the project path
    PROJECT_PATH := $(word 2,$(MAKECMDGOALS))
    # Make the project path a dummy target
    $(eval $(PROJECT_PATH):;@:)
endif

help:
	@echo "$(BLUE)================================$(NC)"
	@echo "$(BLUE)  Monorepo Build System$(NC)"
	@echo "$(BLUE)================================$(NC)"
	@echo ""
	@echo "$(GREEN)Usage:$(NC)"
	@echo "  make init                  - Initialize development environment"
	@echo "  make gen-proto [path]      - Generate code from proto files"
	@echo "  make list-projects [path]  - List all projects (default: all)"
	@echo "  make clean <path>          - Clean build artifacts (project or directory)"
	@echo "  make install <path>        - Install dependencies (project or directory)"
	@echo "  make build <path>          - Build project(s) (project or directory)"
	@echo "  make test <path>           - Run tests (project or directory)"
	@echo "  make run <path>            - Run project (single project only)"
	@echo "  make lint <path>           - Run linter (project or directory)"
	@echo "  make format <path>         - Format code (project or directory)"
	@echo ""
	@echo "$(GREEN)Examples:$(NC)"
	@echo "  make gen-proto               # Auto-detect changed proto files and generate"
	@echo "  make gen-proto user          # Generate code only for user proto files"
	@echo "  make gen-proto user/v1       # Generate code for specific version"
	@echo "  make list-projects           # List all projects"
	@echo "  make build services          # Build all projects in services/"
	@echo "  make build .                 # Build all projects in monorepo"
	@echo "  make test services/user      # Test single project"
	@echo "  make clean .                 # Clean all projects"
	@echo ""
	@echo "$(YELLOW)Supported project types:$(NC)"
	@echo "  - Go (go.mod)"
	@echo "  - Java/Gradle (build.gradle)"
	@echo "  - Node.js (package.json)"

init:
	@$(BUILD_SCRIPT) init

install-tools:
	@$(BUILD_SCRIPT) install-tools

list-projects:
	@if [ -z "$(PROJECT_PATH)" ]; then \
		$(BUILD_SCRIPT) list-projects .; \
	else \
		$(BUILD_SCRIPT) list-projects $(PROJECT_PATH); \
	fi

# Generate proto code
gen-proto:
	@if [ -z "$(PROJECT_PATH)" ]; then \
		$(BUILD_SCRIPT) gen-proto; \
	else \
		$(BUILD_SCRIPT) gen-proto $(PROJECT_PATH); \
	fi

check-project:
ifndef PROJECT_PATH
	@echo "$(RED)✗ Error: project path is required$(NC)"
	@echo "$(YELLOW)Usage: make <command> <path>$(NC)"
	@echo "$(YELLOW)Example: make build services/user-service$(NC)"
	@exit 1
endif
	@if [ ! -d "$(PROJECT_PATH)" ]; then \
		echo "$(RED)✗ Error: Project directory '$(PROJECT_PATH)' does not exist$(NC)"; \
		exit 1; \
	fi

# Clean build artifacts
clean: check-project
	@$(BUILD_SCRIPT) clean $(PROJECT_PATH)

# Install dependencies
install: check-project
	@$(BUILD_SCRIPT) install $(PROJECT_PATH)

# Build project
build: check-project
	@$(BUILD_SCRIPT) build $(PROJECT_PATH)

# Run tests
test: check-project
	@$(BUILD_SCRIPT) test $(PROJECT_PATH)

# Run project
run: check-project
	@$(BUILD_SCRIPT) run $(PROJECT_PATH)

# Lint project
lint: check-project
	@$(BUILD_SCRIPT) lint $(PROJECT_PATH)

# Format code
format: check-project
	@$(BUILD_SCRIPT) format $(PROJECT_PATH)

# Docker build
docker-build: check-project
	@echo "$(BLUE)Building Docker image for $(PROJECT_PATH)...$(NC)"
	@if [ ! -f "$(PROJECT_PATH)/Dockerfile" ]; then \
		echo "$(RED)✗ Dockerfile not found in $(PROJECT_PATH)$(NC)"; \
		exit 1; \
	fi
	@PROJECT_NAME=$$(basename $(PROJECT_PATH)); \
	docker build -t $$PROJECT_NAME:latest $(PROJECT_PATH)
	@echo "$(GREEN)✓ Docker image built for $(PROJECT_PATH)$(NC)"

version:
	@echo "$(BLUE)Tool Versions:$(NC)"
	@echo "  Go: $$(go version 2>/dev/null || echo 'not installed')"
	@echo "  Node: $$(node --version 2>/dev/null || echo 'not installed')"
	@echo "  npm: $$(npm --version 2>/dev/null || echo 'not installed')"
	@echo "  Java: $$(java -version 2>&1 | head -n 1 || echo 'not installed')"
	@echo "  Buf: $$(buf --version 2>/dev/null || echo 'not installed')"
