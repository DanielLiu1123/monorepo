#!/usr/bin/env bash

install_tools() {
    print_info "Installing required development tools..."

    # Check for Java (required)
    if ! command -v java >/dev/null 2>&1; then
        print_error "Java not found. Please install Java manually."
        exit 1
    fi

    if ! command -v go >/dev/null 2>&1; then
        print_info "Installing Go..."
        brew install go
    fi

    if ! command -v node >/dev/null 2>&1; then
        print_info "Installing Node.js..."
        brew install node
    fi

    if ! command -v buf >/dev/null 2>&1; then
        print_info "Installing Buf..."
        brew install bufbuild/buf/buf
    fi

    if ! command -v golangci-lint >/dev/null 2>&1; then
        print_info "Installing golangci-lint..."
        brew install golangci-lint
    fi

    if ! command -v protoc >/dev/null 2>&1; then
        print_info "Installing protoc..."
        brew install protobuf
    fi

    if ! command -v protoc-gen-grpc-java >/dev/null 2>&1; then
        print_info "Installing protoc-gen-grpc-java..."
        brew install protoc-gen-grpc-java
    fi

    if ! command -v protoc-gen-go >/dev/null 2>&1; then
        print_info "Installing protoc-gen-go..."
        brew install protoc-gen-go
    fi

    if ! command -v protoc-gen-go-grpc >/dev/null 2>&1; then
        print_info "Installing protoc-gen-go-grpc..."
        brew install protoc-gen-go-grpc
    fi

    print_success "All required tools installed"
}

cmd_init() {
    print_info "Initializing development environment..."

    print_info "Configuring git hooks..."
    git config core.hooksPath ./scripts/githooks

    install_tools

    print_success "Development environment initialized"
}
