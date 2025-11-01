# Build System Documentation

## Overview

This monorepo uses a centralized build system located in `scripts/build/` that supports multiple project types (Go, Gradle, npm) and allows for project-level customization.

## Basic Usage

```bash
# Build a single project
./scripts/build/main.sh build services/user-service

# Build all projects in a directory
./scripts/build/main.sh build services

# Build all projects in the monorepo
./scripts/build/main.sh build .
```

## Available Commands

- `init` - Initialize development environment
- `gen-proto [path]` - Generate code from proto files
- `list-projects [path]` - List all projects in monorepo
- `clean <path>` - Clean build artifacts
- `install <path>` - Install dependencies
- `build <path>` - Build project(s)
- `test <path>` - Run tests
- `run <path>` - Run project (single project only)
- `lint <path>` - Run linter
- `format <path>` - Format code

## Project-Level Customization

Each project can define custom build behavior by creating a `build.sh` file in the project root. See [PROJECT_BUILD_SCRIPT.md](../scripts/build/PROJECT_BUILD_SCRIPT.md) for detailed documentation.

### Quick Example

1. Copy the template:
   ```bash
   cp scripts/build/build.sh.template services/my-service/build.sh
   chmod +x services/my-service/build.sh
   ```

2. Define custom functions:
   ```bash
   #!/usr/bin/env bash

   build() {
       local project_path="$1"
       print_info "Running custom build"
       execute_command "cd $project_path && npm run build:custom"
       return $?
   }
   ```

3. Run the build:
   ```bash
   ./scripts/build/main.sh build services/my-service
   ```

### Available Functions

You can define these functions in your project's `build.sh` to override default behavior:

- `clean` - Override clean command
- `build` - Override build command
- `format` - Override format command
- `install` - Override install command
- `lint` - Override lint command
- `run` - Override run command
- `test` - Override test command

## Default Behaviors

### Go Projects

**Library (detected by absence of `cmd/` directory):**
- `clean`: Remove `build/` directory
- `install`: `go mod download`
- `build`: `go build ./...` + run tests if test files exist
- `test`: `go test ./... -v`
- `lint`: `golangci-lint run`
- `format`: `go fmt ./...`

**Application (detected by presence of `cmd/` directory):**
- `clean`: Remove `build/` directory
- `install`: `go mod download`
- `build`: Compile all cmd subdirectories to executables in `build/`
- `test`: `go test ./... -v`
- `run`: Run the first executable found in `cmd/`
- `lint`: `golangci-lint run`
- `format`: `go fmt ./...`

### Gradle Projects

**Library (detected by absence of Spring Boot plugin):**
- `clean`: `./gradlew clean`
- `install`: (Dependencies managed by Gradle)
- `build`: `./gradlew jar`
- `test`: `./gradlew test`
- `lint`: `./gradlew check`
- `format`: `./gradlew spotlessApply`

**Application (detected by Spring Boot plugin):**
- `clean`: `./gradlew clean`
- `install`: (Dependencies managed by Gradle)
- `build`: `./gradlew bootJar`
- `test`: `./gradlew test`
- `run`: `./gradlew bootRun`
- `lint`: `./gradlew check`
- `format`: `./gradlew spotlessApply`

### npm Projects

**Library (detected by `main`, `exports`, or `types` field in package.json):**
- `clean`: Remove `dist/`, `build/`, `node_modules/`
- `install`: `npm install`
- `build`: `npm run build` or `npx tsc` if no build script
- `test`: `npm test` if test script exists
- `lint`: `npm run lint` if lint script exists
- `format`: `npx prettier --write .`

**Application (detected by `private: true` or start/dev scripts):**
- `clean`: Remove `dist/`, `build/`, `node_modules/`
- `install`: `npm install`
- `build`: `npm run build` if build script exists
- `test`: `npm test` if test script exists
- `run`: Try `npm start`, `npm run dev`, or `npm run serve` in order
- `lint`: `npm run lint` if lint script exists
- `format`: `npx prettier --write .`

## Advanced Usage

### Running Commands for Multiple Projects

```bash
# Clean all services
./scripts/build/main.sh clean services

# Install dependencies for all apps
./scripts/build/main.sh install apps

# Test all packages
./scripts/build/main.sh test packages
```

### Proto Generation

```bash
# Auto-detect and generate all changed proto files
./scripts/build/main.sh gen-proto

# Generate specific proto
./scripts/build/main.sh gen-proto user

# Generate specific version
./scripts/build/main.sh gen-proto user/v1
```

### Listing Projects

```bash
# List all projects
./scripts/build/main.sh list-projects

# List projects in specific directory
./scripts/build/main.sh list-projects services
```

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Build and Test

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Initialize environment
        run: ./scripts/build/main.sh init

      - name: Install dependencies
        run: ./scripts/build/main.sh install .

      - name: Build all projects
        run: ./scripts/build/main.sh build .

      - name: Run tests
        run: ./scripts/build/main.sh test .

      - name: Run linters
        run: ./scripts/build/main.sh lint .
```

### GitLab CI Example

```yaml
stages:
  - build
  - test

build:
  stage: build
  script:
    - ./scripts/build/main.sh init
    - ./scripts/build/main.sh install .
    - ./scripts/build/main.sh build .

test:
  stage: test
  script:
    - ./scripts/build/main.sh test .
    - ./scripts/build/main.sh lint .
```

## Troubleshooting

### Build fails for unknown project type

**Problem**: Error message "Unknown project type"

**Solution**: Ensure your project has one of:
- `go.mod` for Go projects
- `build.gradle` or `build.gradle.kts` for Gradle projects
- `package.json` for npm projects

### Custom build script not detected

**Problem**: Custom functions not being called

**Solution**: Check:
1. File is named exactly `build.sh` in project root
2. File is executable: `chmod +x build.sh`
3. Function names match the command exactly (e.g., `build`, `clean`, `test`)

### Permission denied errors

**Problem**: "Permission denied" when running scripts

**Solution**:
```bash
chmod +x scripts/build/main.sh
chmod +x <project>/build.sh
```

## Best Practices

1. **Use the build system consistently**: Always use the build scripts rather than running tools directly
2. **Test custom scripts**: Always test custom build scripts with both success and failure scenarios
3. **Document customizations**: Add comments explaining why custom logic is needed
4. **Keep scripts simple**: Prefer simple, maintainable scripts over complex ones
5. **Use utility functions**: Leverage provided utility functions for consistent output
6. **Handle errors properly**: Always return proper exit codes (0 for success, non-zero for failure)

## Architecture

```
scripts/build/
├── main.sh                      # Main entry point and utilities
├── commands/                    # Command implementations
│   ├── build.sh
│   ├── clean.sh
│   ├── format.sh
│   ├── install.sh
│   ├── lint.sh
│   ├── run.sh
│   └── test.sh
├── build.sh.template            # Template for project-level scripts
└── PROJECT_BUILD_SCRIPT.md      # Detailed customization docs
```

## Contributing

When adding new commands or modifying existing ones:

1. Add the command function in `commands/<command>.sh`
2. Source it in `main.sh`
3. Add it to the usage message in `main.sh`
4. Update this documentation
5. Update the template if needed
6. Test with all project types (Go, Gradle, npm)

## See Also

- [Project Build Script Guide](../scripts/build/PROJECT_BUILD_SCRIPT.md) - Detailed guide for project-level customization
- [Template](../scripts/build/build.sh.template) - Template for project-level build scripts
