# Protocol Buffers Specification for AI Agents

This document provides comprehensive guidelines for defining Protocol Buffers (protobuf) messages and services following the project's conventions, specifically designed for AI agents to understand and generate proto definitions.

Protocol Buffers are language-neutral, platform-neutral extensible mechanisms for serializing structured data. This specification is language-agnostic and applies to all services in the monorepo.

## Table of Contents

1. [Basic Structure](#basic-structure)
2. [Model Definition](#model-definition)
3. [Service Definition](#service-definition)
4. [Best Practices](#best-practices)

---

## Basic Structure

### File Location

**Location**: `packages/proto/{domain}/v1/`

### File Naming

- Model definitions: `{entity}.proto`
- Service definitions: `{entity}_service.proto`

### Standard Options

Always include the following options in your proto files:

```protobuf
syntax = "proto3";

package {domain}.v1;

option go_package = "github.com/yourorg/monorepo/packages/proto-gen-go/{domain}/v1;{domain}v1";
option java_package = "monorepo.proto.{domain}.v1";
option java_multiple_files = true;
option java_outer_classname = "{Entity}Proto";
```

**Key Points**:
- Always use `syntax = "proto3"`
- Always include both `go_package` and `java_package` options for multi-language support
- Enable `java_multiple_files = true` for cleaner generated Java code
- Use semantic versioning in package names (e.g., `v1`, `v2`)

---

## Model Definition

### Model Message Structure

Model messages define the core data structures. They typically contain:
- Nested entity messages representing database tables
- Enums for categorical values
- Relationships between entities

### Example: Todo Model

```protobuf
// packages/proto/todo/v1/todo.proto
syntax = "proto3";

package todo.v1;

import "google/protobuf/timestamp.proto";
import "google/type/date.proto";

option go_package = "github.com/yourorg/monorepo/packages/proto-gen-go/todo/v1;todov1";
option java_package = "monorepo.proto.todo.v1";
option java_multiple_files = true;
option java_outer_classname = "TodoProto";

// Main model message
message TodoModel {
  Todo todo = 1;
  repeated SubTask sub_tasks = 3;

  // Nested entity messages
  message Todo {
    int64 id = 1;
    int64 user_id = 2;
    string title = 3;
    string description = 4;
    State state = 5;
    Priority priority = 6;
    optional int64 assignee = 7;              // Use optional for nullable fields
    google.type.Date due_date = 8;
    google.protobuf.Timestamp created_at = 9;
    google.protobuf.Timestamp updated_at = 10;
    optional google.protobuf.Timestamp deleted_at = 11;
  }

  message SubTask {
    int64 id = 1;
    int64 todo_id = 2;
    string title = 3;
    google.protobuf.Timestamp created_at = 5;
    google.protobuf.Timestamp updated_at = 6;
  }

  // Enums: always start with {ENTITY}_UNSPECIFIED = 0
  enum State {
    STATE_UNSPECIFIED = 0;
    PENDING = 1;
    COMPLETED = 2;
  }

  enum Priority {
    PRIORITY_UNSPECIFIED = 0;
    LOW = 1;
    MEDIUM = 2;
    HIGH = 3;
    URGENT = 4;
  }
}
```

### Model Definition Guidelines

1. **Field Numbering**:
   - Use sequential numbering starting from 1
   - Reserve gaps for future fields (e.g., field 2 is intentionally skipped in the example)
   - Never reuse field numbers

2. **Nullable Fields**:
   - Use `optional` keyword for nullable fields in proto3
   - Examples: `optional int64 assignee`, `optional google.protobuf.Timestamp deleted_at`

3. **Timestamps and Dates**:
   - Use `google.protobuf.Timestamp` for datetime fields
   - Use `google.type.Date` for date-only fields
   - Always include `created_at` and `updated_at` timestamps
   - Include `deleted_at` for soft delete support

4. **Enums**:
   - Always start with `{NAME}_UNSPECIFIED = 0` as the default value
   - Use UPPER_SNAKE_CASE for enum value names
   - Define enums inside the model message they belong to

5. **Relationships**:
   - Use `repeated` for one-to-many relationships (e.g., `repeated SubTask sub_tasks`)
   - Use foreign key fields for relationships (e.g., `int64 todo_id`)

---

## Service Definition

### Service Structure

Service definitions specify the RPC methods and request/response messages. Follow these patterns:

### Example: Todo Service

```protobuf
// packages/proto/todo/v1/todo_service.proto
syntax = "proto3";

package todo.v1;

import "todo/v1/todo.proto";
import "google/protobuf/field_mask.proto";
import "google/protobuf/empty.proto";
import "google/type/date.proto";

option go_package = "github.com/yourorg/monorepo/packages/proto-gen-go/todo/v1;todov1";
option java_package = "monorepo.proto.todo.v1";
option java_multiple_files = true;

// Service definition following AIP standards
service TodoService {
  // === Standard CRUD APIs ===

  // Create a new todo
  rpc CreateTodo(CreateTodoRequest) returns (TodoModel) {}

  // Get a todo by ID
  rpc GetTodo(GetTodoRequest) returns (TodoModel) {}

  // List todos with pagination
  rpc ListTodos(ListTodosRequest) returns (ListTodosResponse) {}

  // Update an existing todo
  rpc UpdateTodo(UpdateTodoRequest) returns (TodoModel) {}

  // Delete a todo (soft delete)
  rpc DeleteTodo(DeleteTodoRequest) returns (TodoModel) {}

  // === Additional APIs ===

  // Batch get todos by IDs
  rpc BatchGetTodos(BatchGetTodosRequest) returns (BatchGetTodosResponse) {}
}

// Request messages: separate create/update requests from model
message CreateTodoRequest {
  Todo todo = 1;
  repeated SubTask sub_tasks = 2;

  message Todo {
    // Only include fields needed for creation
    // Exclude: id, created_at, updated_at, deleted_at
    int64 user_id = 1;
    string title = 2;
    optional string description = 3;
    optional TodoModel.State state = 4;
    optional TodoModel.Priority priority = 5;
    optional int64 assignee = 6;
    optional google.type.Date due_date = 7;
  }

  message SubTask {
    string title = 1;
  }
}

message GetTodoRequest {
  int64 id = 1;
  // default to true if not set
  optional bool show_deleted = 2;
}

message ListTodosRequest {
  // Pagination
  int32 page_size = 1;              // Default: 50, Max: 1000
  string page_token = 2;             // Opaque pagination token

  // Parent/tenant filter
  int64 user_id = 3;

  // Filtering
  optional Filter filter = 4;

  // Sorting
  repeated OrderBy order_by = 5;

  // Soft delete handling
  optional bool show_deleted = 6;

  message Filter {
    repeated TodoModel.State states = 1;
    repeated TodoModel.Priority priorities = 2;
  }

  message OrderBy {
    string field = 1;                // e.g., "created_at", "priority"
    bool is_desc = 2;
  }
}

message ListTodosResponse {
  repeated TodoModel todos = 1;
  string next_page_token = 2;       // Empty if no more results
  int32 total_size = 3;
}

message UpdateTodoRequest {
  Todo todo = 1;
  repeated SubTaskOperation sub_task_operations = 2;

  message Todo {
    int64 id = 1;                    // Required for updates
    optional string title = 2;       // All other fields optional
    optional string description = 3;
    optional TodoModel.State state = 4;
    optional TodoModel.Priority priority = 5;
    optional int64 assignee = 6;
    optional google.type.Date due_date = 7;
  }

  message SubTask {
    int64 id = 1;
    optional string title = 2;
  }

  message SubTaskOperation {
    // Support create, update, delete operations
    oneof operation {
      CreateTodoRequest.SubTask create = 1;
      UpdateTodoRequest.SubTask update = 2;
      int64 delete = 3;              // ID to delete
    }
  }
}

message DeleteTodoRequest {
  int64 id = 1;
}

message BatchGetTodosRequest {
  repeated int64 ids = 1;
  optional bool show_deleted = 2;
}

message BatchGetTodosResponse {
  repeated TodoModel todos = 1;
}
```

### Service Definition Guidelines

1. **RPC Methods**:
   - Follow standard CRUD naming: `Create{Entity}`, `Get{Entity}`, `List{Entities}`, `Update{Entity}`, `Delete{Entity}`
   - Use unary RPC for standard operations
   - Add comments describing each RPC method

2. **Request Messages**:
   - **Create Requests**: Exclude auto-generated fields (id, created_at, updated_at, deleted_at)
   - **Update Requests**:
     - Include ID as required field
     - Make all other fields `optional` for partial updates
     - Exclude user_id and other immutable fields
   - **Get Requests**: Include ID and optional flags (e.g., `show_deleted`)
   - **List Requests**: Include pagination, filtering, and sorting fields
   - **Delete Requests**: Typically just the ID

3. **Response Messages**:
   - Return the full model for Create, Get, Update, Delete operations
   - List responses should include:
     - `repeated` field for items
     - `next_page_token` for pagination
     - `total_size` for total count

4. **Pagination**:
   - Always include `page_size` (default: 50, max: 1000)
   - Use opaque `page_token` for cursor-based pagination
   - Return `next_page_token` in response (empty if no more results)

5. **Filtering and Sorting**:
   - Define nested `Filter` message for filter criteria
   - Use `repeated` for multi-select filters (e.g., `repeated State states`)
   - Define `OrderBy` message with `field` and `is_desc` for sorting

6. **Complex Operations**:
   - Use `oneof` for operations that can be one of several types
   - Example: SubTask operations (create, update, delete) in UpdateTodoRequest

7. **Soft Delete**:
   - Include `optional bool show_deleted` parameter in Get and List requests
   - Default behavior should be to hide deleted items unless explicitly requested

---

## Best Practices

### Naming Conventions

1. **Messages**: PascalCase (e.g., `TodoModel`, `CreateTodoRequest`)
2. **Fields**: snake_case (e.g., `user_id`, `created_at`, `sub_tasks`)
3. **Enums**: UPPER_SNAKE_CASE (e.g., `STATE_UNSPECIFIED`, `PRIORITY_HIGH`)
4. **Services**: PascalCase (e.g., `TodoService`)
5. **RPC Methods**: PascalCase (e.g., `CreateTodo`, `ListTodos`)

### Field Types

1. **IDs**: Use `int64` for all ID fields
2. **Booleans**: Use `bool` for flags
3. **Strings**: Use `string` for text fields
4. **Numbers**:
   - Use `int32` for counts and pagination sizes
   - Use `int64` for IDs and large numbers
   - Use `double` for floating-point numbers
5. **Timestamps**: Use `google.protobuf.Timestamp`
6. **Dates**: Use `google.type.Date`

### Common Patterns

1. **Tenant Isolation**:
   - Include tenant/user ID in request messages (e.g., `int64 user_id`)
   - Always filter by tenant in List operations

2. **Nested Entities**:
   - Define nested messages inside the parent model message
   - Use `repeated` for one-to-many relationships

3. **Update Operations**:
   - Use separate request messages for create vs. update
   - Make update fields optional for partial updates
   - For complex nested updates, use `oneof` with operation types

4. **Batch Operations**:
   - Prefix with `Batch` (e.g., `BatchGetTodos`)
   - Accept `repeated` IDs
   - Return `repeated` models

### Versioning

1. **Package Versioning**:
   - Start with `v1` (e.g., `package todo.v1`)
   - Increment version for breaking changes (e.g., `v2`)
   - Keep old versions for backward compatibility

2. **Field Changes**:
   - Never remove or change field numbers
   - Never change field types
   - Use `reserved` for deprecated fields
   - Add new fields with new field numbers

### Documentation

1. **Comments**:
   - Add comments to all messages, fields, and RPCs
   - Document default values
   - Document validation rules
   - Document business logic constraints

2. **Examples**:
   ```protobuf
   // The maximum number of todos to return
   // Default: 50, Max: 1000
   int32 page_size = 1;

   // Filter by todo state
   // If empty, returns all states
   repeated TodoModel.State states = 1;
   ```

---

## Summary Checklist for AI Agents

When defining a new proto service, follow this checklist:

### 1. Model Definition (`{entity}.proto`)
- [ ] Use `syntax = "proto3"`
- [ ] Include both `go_package` and `java_package` options
- [ ] Enable `java_multiple_files = true`
- [ ] Define main model message with nested entities
- [ ] Include all entity fields with appropriate types
- [ ] Use `optional` for nullable fields
- [ ] Use `google.protobuf.Timestamp` for timestamps
- [ ] Use `google.type.Date` for dates
- [ ] Define enums with `{NAME}_UNSPECIFIED = 0`
- [ ] Include `created_at`, `updated_at`, `deleted_at` timestamps

### 2. Service Definition (`{entity}_service.proto`)
- [ ] Define service with standard CRUD operations
- [ ] Create separate request messages for create/update operations
- [ ] Exclude auto-generated fields from create requests
- [ ] Make update fields optional (except ID)
- [ ] Include pagination in List requests (`page_size`, `page_token`)
- [ ] Include filtering and sorting in List requests
- [ ] Return `next_page_token` and `total_size` in List responses
- [ ] Support soft delete with `show_deleted` parameter
- [ ] Use `oneof` for complex operation types
- [ ] Add comments to all messages and RPCs

### 3. Best Practices
- [ ] Follow naming conventions (PascalCase for messages, snake_case for fields)
- [ ] Use appropriate field types (int64 for IDs, int32 for counts)
- [ ] Include tenant/user ID for multi-tenant support
- [ ] Document default values and validation rules
- [ ] Never reuse field numbers
- [ ] Use semantic versioning (v1, v2, etc.)

This specification provides all the patterns and examples needed to define Protocol Buffers messages and services consistently in this monorepo.
