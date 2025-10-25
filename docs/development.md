# Development Guide

## Setup

### Prerequisites

Ensure you have the following tools installed:

- **Go**: 1.21 or higher
- **Node.js**: 18.x or higher
- **Java**: 17 or higher
- **Python**: 3.11 or higher
- **Docker**: Latest version
- **Make**: Latest version

### Initial Setup

```bash
# Clone the repository
git clone <repository-url>
cd monorepo

# Initialize development environment
make init

# Start infrastructure services
cd infrastructure/docker-compose
docker-compose up -d
```

## Proto-First Development

### 1. Define Service Interface

Create a new proto file in `packages/proto/<service>/v1/<service>.proto`:

```protobuf
syntax = "proto3";

package myservice.v1;

service MyService {
  rpc GetItem(GetItemRequest) returns (GetItemResponse) {}
}

message GetItemRequest {
  string id = 1;
}

message GetItemResponse {
  Item item = 1;
}

message Item {
  string id = 1;
  string name = 2;
}
```

### 2. Generate Code

```bash
make proto
```

This generates code for all languages:
- Go: `packages/proto-gen-go/`
- Java: `packages/proto-gen-java/`
- TypeScript: `packages/proto-gen-ts/`
- Python: `packages/proto-gen-python/`

### 3. Implement Service

Implement the generated interface in your chosen language.

## Working with Services

### Go Services

```bash
# Install dependencies
make install project=services/user-service

# Run tests
make test project=services/user-service

# Run service
make run project=services/user-service

# Build binary
make build project=services/user-service
```

### Java Services

```bash
# Build with Gradle
make build project=services/product-service

# Run tests
make test project=services/product-service

# Run service
make run project=services/product-service
```

### Node.js Services

```bash
# Install dependencies
make install project=services/notification-service

# Development mode
make run project=services/notification-service

# Build
make build project=services/notification-service
```

### Python Services

```bash
# Install dependencies
make install project=services/analytics-service

# Run service
make run project=services/analytics-service

# Run tests
make test project=services/analytics-service
```

## Frontend Development

### Web App

```bash
# Install dependencies
make install project=apps/web

# Development mode
make run project=apps/web

# Build for production
make build project=apps/web
```

### Shared Packages

When working on shared packages:

```bash
# Build UI components
cd packages/ui-components
npm run build

# Watch mode
npm run dev
```

Changes to shared packages are automatically picked up by apps that depend on them (thanks to npm workspaces).

## Testing

### Unit Tests

```bash
# Test specific project
make test project=services/user-service

# Test all services
make test-all
```

### Integration Tests

```bash
cd tests/integration
npm test
```

### E2E Tests

```bash
cd tests/e2e
npm test
```

## Code Quality

### Linting

```bash
# Lint specific project
make lint project=services/user-service

# Go
cd services/user-service
golangci-lint run

# Java
cd services/product-service
gradle checkstyleMain

# Node.js
cd services/notification-service
npm run lint

# Python
cd services/analytics-service
poetry run ruff check .
```

### Formatting

```bash
# Go
gofmt -w .

# Java
gradle spotlessApply

# Node.js/TypeScript
npm run format

# Python
poetry run black .
```

## Debugging

### Go Services

```bash
# Run with debugger
dlv debug ./cmd/main.go
```

### Java Services

Use your IDE's debugger with Spring Boot configuration.

### Node.js Services

```bash
# Debug mode
node --inspect-brk dist/main.js
```

### Python Services

```bash
# Debug mode
poetry run python -m debugpy --listen 5678 -m app.main
```

## Environment Variables

Each service uses environment variables for configuration:

```bash
# Example .env file
DB_HOST=localhost
DB_PORT=5432
DB_NAME=mydb
REDIS_URL=redis://localhost:6379
JAEGER_ENDPOINT=http://localhost:14268/api/traces
```

## Database Migrations

### Go Services (golang-migrate)

```bash
migrate -path migrations -database "postgresql://..." up
```

### Java Services (Flyway)

Migrations are automatically applied on startup.

### Python Services (Alembic)

```bash
poetry run alembic upgrade head
```

## Adding a New Service

1. **Create directory structure**
   ```bash
   mkdir -p services/my-service
   ```

2. **Add configuration files**
   - Go: `go.mod`
   - Java: `build.gradle`
   - Node.js: `package.json`
   - Python: `pyproject.toml`

3. **Update workspace configs**
   - Add to `go.work` (Go services)
   - Add to `settings.gradle` (Java services)
   - Add to root `package.json` workspaces (Node.js services)

4. **Define proto interface**
   ```bash
   mkdir -p packages/proto/my-service/v1
   # Create my-service.proto
   ```

5. **Generate code**
   ```bash
   make proto
   ```

6. **Implement service**

7. **Add tests**

8. **Update documentation**

## Best Practices

### 1. Always define proto first
Before implementing a service, define its interface in proto.

### 2. Use shared libraries
Don't duplicate code. Use shared libraries for common functionality.

### 3. Write tests
Aim for high test coverage. Write unit tests and integration tests.

### 4. Keep services small
Each service should have a single responsibility.

### 5. Use semantic versioning
Version your services and packages semantically.

### 6. Document your code
Write clear comments and maintain up-to-date documentation.

### 7. Follow language conventions
- Go: Follow Go best practices
- Java: Follow Spring Boot conventions
- Node.js: Use TypeScript strict mode
- Python: Follow PEP 8

### 8. Use feature branches
Create feature branches for new work. Don't commit directly to main.

### 9. Review PRs
All code must be reviewed before merging.

### 10. Monitor your services
Add metrics, logging, and tracing to all services.

## Troubleshooting

### Proto generation fails

```bash
# Check buf version
buf --version

# Update buf
go install github.com/bufbuild/buf/cmd/buf@latest

# Clean and regenerate
rm -rf packages/proto-gen-*
make proto
```

### Go workspace issues

```bash
# Sync workspace
go work sync

# Update dependencies
go work use ./services/my-service
```

### Gradle issues

```bash
# Clean build
./gradlew clean build --refresh-dependencies
```

### npm workspace issues

```bash
# Clean install
rm -rf node_modules package-lock.json
npm install
```

## Getting Help

- Check documentation in `docs/`
- Ask in team Slack channel
- Create an issue in GitHub
- Contact the platform team
