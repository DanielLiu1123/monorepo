# Enterprise Monorepo

A production-ready enterprise monorepo with multi-language microservices architecture.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend Layer                        │
│  ┌──────────┐    ┌───────────┐    ┌──────────────┐         │
│  │   Web    │    │   Admin   │    │    Mobile    │         │
│  │ (Next.js)│    │ (Next.js) │    │ (React Native)│        │
│  └────┬─────┘    └─────┬─────┘    └──────┬───────┘         │
└───────┼────────────────┼──────────────────┼─────────────────┘
        │                │                  │
┌───────┼────────────────┼──────────────────┼─────────────────┐
│       │          BFF Layer (gRPC)         │                 │
│  ┌────▼─────┐    ┌─────▼────┐    ┌───────▼─────┐          │
│  │ Web BFF  │    │Admin BFF │    │ Mobile BFF  │          │
│  │  (Go)    │    │   (Go)   │    │    (Go)     │          │
│  └────┬─────┘    └─────┬────┘    └───────┬─────┘          │
└───────┼────────────────┼──────────────────┼─────────────────┘
        │                │                  │
┌───────┴────────────────┴──────────────────┴─────────────────┐
│                  Service Layer (gRPC)                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │   User   │  │ Product  │  │  Order   │  │ Payment  │   │
│  │   (Go)   │  │ (Java)   │  │ (Java)   │  │   (Go)   │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
│                                                              │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                 │
│  │  Notify  │  │  Search  │  │Analytics │                 │
│  │(Node.js) │  │ (Java)   │  │ (Python) │                 │
│  └──────────┘  └──────────┘  └──────────┘                 │
└──────────────────────────────────────────────────────────────┘
```

## Project Structure

```
.
├── apps/                       # Deployable applications
│   ├── web/                    # Web application (Next.js)
│   ├── admin/                  # Admin dashboard (Next.js)
│   ├── mobile/                 # Mobile app (React Native)
│   ├── web-bff/                # BFF for web (Go)
│   ├── admin-bff/              # BFF for admin (Go)
│   └── mobile-bff/             # BFF for mobile (Go)
│
├── services/                   # Backend microservices
│   ├── user-service/           # User management (Go)
│   ├── product-service/        # Product catalog (Java/Spring Boot)
│   ├── order-service/          # Order management (Java/Spring Boot)
│   ├── payment-service/        # Payment processing (Go)
│   ├── notification-service/   # Notifications (Node.js/TypeScript)
│   ├── search-service/         # Search (Java/Elasticsearch)
│   └── analytics-service/      # Analytics (Python/FastAPI)
│
├── packages/                   # Shared packages
│   ├── proto/                  # Protobuf definitions (IDL)
│   ├── proto-gen-go/           # Generated Go code
│   ├── proto-gen-java/         # Generated Java code
│   ├── proto-gen-ts/           # Generated TypeScript code
│   ├── proto-gen-python/       # Generated Python code
│   ├── go-lib/                 # Go shared libraries
│   ├── java-lib/               # Java shared libraries
│   ├── ts-lib/                 # TypeScript shared libraries
│   ├── python-lib/             # Python shared libraries
│   ├── grpc-clients/           # gRPC client wrappers
│   ├── ui-components/          # Shared UI components
│   └── api-client/             # Frontend API client
│
├── infrastructure/             # Infrastructure as Code
│   ├── kubernetes/             # K8s manifests
│   ├── terraform/              # Terraform configs
│   ├── helm/                   # Helm charts
│   └── docker-compose/         # Local development
│
├── scripts/                    # Build and deployment scripts
│   ├── build/                  # Build scripts
│   ├── deploy/                 # Deployment scripts
│   ├── proto/                  # Proto generation scripts
│   └── init/                   # Initialization scripts
│
├── docs/                       # Documentation
│   ├── architecture/           # Architecture docs
│   ├── api/                    # API documentation
│   └── deployment/             # Deployment guides
│
├── tests/                      # Integration and E2E tests
│   ├── integration/            # Integration tests
│   └── e2e/                    # End-to-end tests
│
├── Makefile                    # Unified build interface
├── package.json                # NPM workspace config
├── turbo.json                  # Turborepo config
├── go.work                     # Go workspace config
├── settings.gradle             # Gradle multi-project config
└── build.gradle                # Root Gradle config
```

## Technology Stack

### Frontend
- **Framework**: Next.js 14, React 18
- **Language**: TypeScript
- **State Management**: Zustand / React Query
- **Styling**: Tailwind CSS
- **Build Tool**: Turbo

### Backend Services
- **User Service**: Go 1.21 + gRPC
- **Product Service**: Java 17 + Spring Boot 3 + Gradle
- **Order Service**: Java 17 + Spring Boot 3 + Gradle
- **Payment Service**: Go 1.21 + gRPC
- **Notification Service**: Node.js 18 + TypeScript + NestJS
- **Search Service**: Java 17 + Spring Boot 3 + Elasticsearch
- **Analytics Service**: Python 3.11 + FastAPI

### Communication
- **Protocol**: gRPC (HTTP/2)
- **IDL**: Protocol Buffers (proto3)
- **API Gateway**: BFF pattern with Go

### Infrastructure
- **Container**: Docker
- **Orchestration**: Kubernetes
- **IaC**: Terraform
- **CI/CD**: GitHub Actions
- **Monitoring**: Prometheus + Grafana
- **Logging**: ELK Stack
- **Tracing**: Jaeger

## Getting Started

### Prerequisites

- Go 1.21+
- Node.js 18+
- Java 17+
- Python 3.11+
- Docker & Docker Compose
- Buf CLI
- Make

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd monorepo
   ```

2. **Initialize development environment**
   ```bash
   make init
   ```
   This will:
   - Check for required tools
   - Install additional tools (buf, golangci-lint, poetry)
   - Initialize npm workspace
   - Set up Go workspace

3. **Generate proto code**
   ```bash
   # Generate all proto code
   make gen-proto

   # Or generate only specific path
   make gen-proto user
   make gen-proto user/v1

   # Auto-detect changed proto files (smart mode)
   make gen-proto
   ```

### Building Projects

The Makefile provides a unified interface for all project types:

```bash
# Build a specific project
make build project=services/user-service
make build project=apps/web

# Install dependencies
make install project=services/product-service

# Run tests
make test project=services/order-service

# Run a service
make run project=services/payment-service

# Clean build artifacts
make clean project=apps/admin

# Lint code
make lint project=services/notification-service
```

### Build All Services

```bash
# Build all services and apps
make build-all

# Test all services
make test-all
```

### Running Services Locally

#### Using Docker Compose

```bash
cd infrastructure/docker-compose
docker-compose up -d
```

#### Running Individual Services

```bash
# User service
make run project=services/user-service

# Product service
make run project=services/product-service

# Web app
make run project=apps/web
```

## Development Workflow

### 1. Proto-First Development

1. Define your service interface in `packages/proto/`
2. Generate code:
   ```bash
   # Auto-detect changes and generate
   make gen-proto

   # Or generate specific service
   make gen-proto user/v1
   ```
3. Implement the service in your chosen language
4. Use generated clients to consume the service

### 2. Adding a New Service

```bash
# Create service directory
mkdir -p services/my-service

# Add necessary files (go.mod, package.json, build.gradle, etc.)

# Update workspace configs
# - Add to go.work (for Go services)
# - Add to settings.gradle (for Java services)
# - Add to package.json workspaces (for Node.js services)

# Build and test
make build project=services/my-service
make test project=services/my-service
```

### 3. Working with Shared Libraries

Shared libraries are organized by language:

```bash
# Go
packages/go-lib/{logger,tracer,metrics,config}

# Java
packages/java-lib/{logger,tracer,metrics,config}

# TypeScript
packages/ts-lib/{logger,tracer,metrics,config}

# Python
packages/python-lib/{logger,tracer,metrics,config}
```

## Makefile Commands

| Command | Description | Example |
|---------|-------------|---------|
| `make help` | Show help message | `make help` |
| `make init` | Initialize dev environment | `make init` |
| `make gen-proto` | Generate proto code (smart) | `make gen-proto` or `make gen-proto user` |
| `make install` | Install dependencies | `make install project=apps/web` |
| `make build` | Build project | `make build project=services/user-service` |
| `make test` | Run tests | `make test project=services/order-service` |
| `make run` | Run project | `make run project=apps/admin` |
| `make clean` | Clean artifacts | `make clean project=services/payment-service` |
| `make lint` | Run linter | `make lint project=services/search-service` |
| `make build-all` | Build all projects | `make build-all` |
| `make test-all` | Test all projects | `make test-all` |
| `make version` | Show tool versions | `make version` |

## Configuration

### Tool Versions (Locked)

Versions are locked in the Makefile to ensure consistency:

- Buf: 1.28.1
- Go: 1.21
- Node.js: 18.18.0
- Gradle: 8.5
- Poetry: 1.7.1
- golangci-lint: 1.55.2

## Architecture Principles

1. **Clean Separation**: Apps → BFF → Services → Database
2. **Type Safety**: Proto definitions ensure cross-language type safety
3. **Independent Deployment**: Each service can be deployed independently
4. **Code Sharing**: Shared libraries reduce duplication
5. **Language Agnostic**: Use the best tool for each job
6. **API Gateway Pattern**: BFF layer handles frontend-specific needs

## Documentation

- [Architecture Overview](docs/architecture/overview.md)
- [API Documentation](docs/api/README.md)
- [Deployment Guide](docs/deployment/README.md)
- [Development Guidelines](docs/development.md)

## CI/CD

The project uses GitHub Actions for CI/CD:

- **Build**: All services are built on every commit
- **Test**: Comprehensive test suite runs automatically
- **Deploy**: Automated deployment to staging/production

## Monitoring & Observability

- **Metrics**: Prometheus metrics exported by all services
- **Logging**: Structured logging with consistent format
- **Tracing**: Distributed tracing with Jaeger
- **Dashboards**: Pre-configured Grafana dashboards

## Contributing

1. Create a feature branch
2. Make your changes
3. Add tests
4. Run `make test-all`
5. Submit a pull request

## License

MIT License - see LICENSE file for details
