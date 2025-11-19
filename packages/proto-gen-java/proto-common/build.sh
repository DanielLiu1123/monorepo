#!/usr/bin/env bash

fmt() {
  print_info "Skipping formatting for generated code"
  return 0
}

lint() {
  print_info "Skipping linting for generated code"
  return 0
}

test() {
  print_info "Skipping tests for generated code"
  return 0
}
