# Project Structure

```
monorepo/
├── apps/                                    # Deployable applications
│   ├── web/                                 # Web application (Next.js)
│   │   ├── src/
│   │   ├── public/
│   │   └── package.json
│   ├── admin/                               # Admin dashboard (Next.js)
│   │   ├── src/
│   │   ├── public/
│   │   └── package.json
│   ├── mobile/                              # Mobile app (React Native)
│   │   ├── src/
│   │   ├── public/
│   │   └── package.json
│   ├── web-bff/                             # BFF for web (Go)
│   │   ├── cmd/
│   │   ├── internal/
│   │   ├── pkg/
│   │   └── go.mod
│   ├── admin-bff/                           # BFF for admin (Go)
│   │   ├── cmd/
│   │   ├── internal/
│   │   ├── pkg/
│   │   └── go.mod
│   └── mobile-bff/                          # BFF for mobile (Go)
│       ├── cmd/
│       ├── internal/
│       ├── pkg/
│       └── go.mod
│
├── services/                                # Backend microservices
│   ├── user-service/                        # User management (Go)
│   │   ├── cmd/
│   │   │   └── main.go
│   │   ├── internal/
│   │   ├── pkg/
│   │   └── go.mod
│   ├── product-service/                     # Product catalog (Java/Spring Boot)
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   └── resources/
│   │   │   └── test/
│   │   ├── gradle/
│   │   └── build.gradle
│   ├── order-service/                       # Order management (Java/Spring Boot)
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   └── resources/
│   │   │   └── test/
│   │   ├── gradle/
│   │   └── build.gradle
│   ├── payment-service/                     # Payment processing (Go)
│   │   ├── cmd/
│   │   ├── internal/
│   │   ├── pkg/
│   │   └── go.mod
│   ├── notification-service/                # Notifications (Node.js/TypeScript)
│   │   ├── src/
│   │   ├── tests/
│   │   ├── package.json
│   │   └── tsconfig.json
│   ├── search-service/                      # Search (Java/Elasticsearch)
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   └── resources/
│   │   │   └── test/
│   │   ├── gradle/
│   │   └── build.gradle
│   └── analytics-service/                   # Analytics (Python/FastAPI)
│       ├── app/
│       │   ├── __init__.py
│       │   └── main.py
│       ├── tests/
│       └── pyproject.toml
│
├── packages/                                # Shared packages
│   ├── proto/                               # Protobuf definitions (IDL)
│   │   ├── user/
│   │   │   └── v1/
│   │   │       └── user.proto
│   │   ├── product/
│   │   │   └── v1/
│   │   │       └── product.proto
│   │   ├── order/
│   │   ├── payment/
│   │   ├── notification/
│   │   ├── search/
│   │   ├── analytics/
│   │   ├── buf.yaml
│   │   └── buf.gen.yaml
│   ├── proto-gen-go/                        # Generated Go code
│   │   └── go.mod
│   ├── proto-gen-java/                      # Generated Java code
│   ├── proto-gen-ts/                        # Generated TypeScript code
│   ├── proto-gen-python/                    # Generated Python code
│   ├── go-lib/                              # Go shared libraries
│   │   ├── logger/
│   │   │   └── go.mod
│   │   ├── tracer/
│   │   │   └── go.mod
│   │   ├── metrics/
│   │   │   └── go.mod
│   │   └── config/
│   │       └── go.mod
│   ├── java-lib/                            # Java shared libraries
│   │   ├── logger/
│   │   ├── tracer/
│   │   ├── metrics/
│   │   └── config/
│   ├── ts-lib/                              # TypeScript shared libraries
│   │   ├── logger/
│   │   ├── tracer/
│   │   ├── metrics/
│   │   └── config/
│   ├── python-lib/                          # Python shared libraries
│   │   ├── logger/
│   │   ├── tracer/
│   │   ├── metrics/
│   │   └── config/
│   ├── grpc-clients/                        # gRPC client wrappers
│   │   ├── src/
│   │   └── go.mod
│   ├── ui-components/                       # Shared UI components
│   │   ├── src/
│   │   └── package.json
│   └── api-client/                          # Frontend API client
│       ├── src/
│       └── package.json
│
├── infrastructure/                          # Infrastructure as Code
│   ├── kubernetes/                          # K8s manifests
│   │   ├── apps/
│   │   ├── services/
│   │   └── base/
│   ├── terraform/                           # Terraform configs
│   │   ├── aws/
│   │   ├── gcp/
│   │   └── azure/
│   ├── helm/                                # Helm charts
│   │   └── charts/
│   └── docker-compose/                      # Local development
│       ├── docker-compose.yml
│       └── prometheus.yml
│
├── scripts/                                 # Build and deployment scripts
│   ├── build/
│   ├── deploy/
│   ├── proto/
│   └── init/
│       └── setup.sh
│
├── docs/                                    # Documentation
│   ├── architecture/
│   │   └── overview.md
│   ├── api/
│   ├── deployment/
│   └── development.md
│
├── tests/                                   # Integration and E2E tests
│   ├── integration/
│   └── e2e/
│
├── Makefile                                 # Unified build interface
├── package.json                             # NPM workspace config
├── turbo.json                               # Turborepo config
├── go.work                                  # Go workspace config
├── settings.gradle                          # Gradle multi-project config
├── build.gradle                             # Root Gradle config
├── .gitignore                               # Git ignore patterns
├── README.md                                # Project documentation
└── STRUCTURE.md                             # This file
```

## Key Directories

### `/apps`
Contains all deployable applications including frontend apps and BFF services.

### `/services`
Backend microservices implementing business logic. Each service is independent and can be deployed separately.

### `/packages`
Shared code and libraries used across multiple services and apps.

### `/infrastructure`
Infrastructure configuration files for Kubernetes, Terraform, Helm, and local development.

### `/scripts`
Utility scripts for building, deploying, and managing the monorepo.

### `/docs`
Project documentation including architecture, API specs, and deployment guides.

### `/tests`
Integration and end-to-end tests that span multiple services.

## Configuration Files

- **Makefile**: Unified build system for all project types
- **package.json**: NPM workspace configuration
- **turbo.json**: Turborepo configuration for faster builds
- **go.work**: Go workspace for multi-module development
- **settings.gradle**: Gradle multi-project configuration
- **build.gradle**: Root Gradle configuration with shared dependencies
- **buf.yaml**: Protobuf linting and breaking change detection
- **buf.gen.yaml**: Protobuf code generation configuration

## Technology Stack by Service

| Service | Language | Framework | Communication |
|---------|----------|-----------|---------------|
| User Service | Go | gRPC | gRPC |
| Product Service | Java | Spring Boot | gRPC |
| Order Service | Java | Spring Boot | gRPC |
| Payment Service | Go | gRPC | gRPC |
| Notification Service | Node.js | NestJS | gRPC |
| Search Service | Java | Spring Boot + ES | gRPC |
| Analytics Service | Python | FastAPI | gRPC + HTTP |
| Web BFF | Go | Gin | HTTP → gRPC |
| Admin BFF | Go | Gin | HTTP → gRPC |
| Mobile BFF | Go | Gin | HTTP → gRPC |

## Build System

The Makefile provides a unified interface for all project types:

```bash
make build project=services/user-service    # Go
make build project=services/product-service # Java/Gradle
make build project=services/notification-service # Node.js
make build project=services/analytics-service # Python
make build project=apps/web                 # Next.js
```

## Dependencies

### Inter-service Dependencies
Services communicate via gRPC using shared proto definitions. No direct code dependencies between services.

### Shared Libraries
- Go services → packages/go-lib/*
- Java services → packages/java-lib/*
- Node.js services → packages/ts-lib/*
- Python services → packages/python-lib/*

### Generated Code
All services depend on generated proto code:
- Go: packages/proto-gen-go
- Java: packages/proto-gen-java
- TypeScript: packages/proto-gen-ts
- Python: packages/proto-gen-python
