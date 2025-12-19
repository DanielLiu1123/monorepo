package monorepo.proto.todo.v1;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * TodoService provides CRUD operations for todos following AIP standards
 * </pre>
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class TodoServiceGrpc {

  private TodoServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "monorepo.todo.v1.TodoService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<monorepo.proto.todo.v1.CreateTodoRequest,
      monorepo.proto.todo.v1.Todo> getCreateTodoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateTodo",
      requestType = monorepo.proto.todo.v1.CreateTodoRequest.class,
      responseType = monorepo.proto.todo.v1.Todo.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<monorepo.proto.todo.v1.CreateTodoRequest,
      monorepo.proto.todo.v1.Todo> getCreateTodoMethod() {
    io.grpc.MethodDescriptor<monorepo.proto.todo.v1.CreateTodoRequest, monorepo.proto.todo.v1.Todo> getCreateTodoMethod;
    if ((getCreateTodoMethod = TodoServiceGrpc.getCreateTodoMethod) == null) {
      synchronized (TodoServiceGrpc.class) {
        if ((getCreateTodoMethod = TodoServiceGrpc.getCreateTodoMethod) == null) {
          TodoServiceGrpc.getCreateTodoMethod = getCreateTodoMethod =
              io.grpc.MethodDescriptor.<monorepo.proto.todo.v1.CreateTodoRequest, monorepo.proto.todo.v1.Todo>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateTodo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  monorepo.proto.todo.v1.CreateTodoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  monorepo.proto.todo.v1.Todo.getDefaultInstance()))
              .setSchemaDescriptor(new TodoServiceMethodDescriptorSupplier("CreateTodo"))
              .build();
        }
      }
    }
    return getCreateTodoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<monorepo.proto.todo.v1.GetTodoRequest,
      monorepo.proto.todo.v1.Todo> getGetTodoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetTodo",
      requestType = monorepo.proto.todo.v1.GetTodoRequest.class,
      responseType = monorepo.proto.todo.v1.Todo.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<monorepo.proto.todo.v1.GetTodoRequest,
      monorepo.proto.todo.v1.Todo> getGetTodoMethod() {
    io.grpc.MethodDescriptor<monorepo.proto.todo.v1.GetTodoRequest, monorepo.proto.todo.v1.Todo> getGetTodoMethod;
    if ((getGetTodoMethod = TodoServiceGrpc.getGetTodoMethod) == null) {
      synchronized (TodoServiceGrpc.class) {
        if ((getGetTodoMethod = TodoServiceGrpc.getGetTodoMethod) == null) {
          TodoServiceGrpc.getGetTodoMethod = getGetTodoMethod =
              io.grpc.MethodDescriptor.<monorepo.proto.todo.v1.GetTodoRequest, monorepo.proto.todo.v1.Todo>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetTodo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  monorepo.proto.todo.v1.GetTodoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  monorepo.proto.todo.v1.Todo.getDefaultInstance()))
              .setSchemaDescriptor(new TodoServiceMethodDescriptorSupplier("GetTodo"))
              .build();
        }
      }
    }
    return getGetTodoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<monorepo.proto.todo.v1.ListTodosRequest,
      monorepo.proto.todo.v1.ListTodosResponse> getListTodosMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListTodos",
      requestType = monorepo.proto.todo.v1.ListTodosRequest.class,
      responseType = monorepo.proto.todo.v1.ListTodosResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<monorepo.proto.todo.v1.ListTodosRequest,
      monorepo.proto.todo.v1.ListTodosResponse> getListTodosMethod() {
    io.grpc.MethodDescriptor<monorepo.proto.todo.v1.ListTodosRequest, monorepo.proto.todo.v1.ListTodosResponse> getListTodosMethod;
    if ((getListTodosMethod = TodoServiceGrpc.getListTodosMethod) == null) {
      synchronized (TodoServiceGrpc.class) {
        if ((getListTodosMethod = TodoServiceGrpc.getListTodosMethod) == null) {
          TodoServiceGrpc.getListTodosMethod = getListTodosMethod =
              io.grpc.MethodDescriptor.<monorepo.proto.todo.v1.ListTodosRequest, monorepo.proto.todo.v1.ListTodosResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListTodos"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  monorepo.proto.todo.v1.ListTodosRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  monorepo.proto.todo.v1.ListTodosResponse.getDefaultInstance()))
              .setSchemaDescriptor(new TodoServiceMethodDescriptorSupplier("ListTodos"))
              .build();
        }
      }
    }
    return getListTodosMethod;
  }

  private static volatile io.grpc.MethodDescriptor<monorepo.proto.todo.v1.UpdateTodoRequest,
      monorepo.proto.todo.v1.Todo> getUpdateTodoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateTodo",
      requestType = monorepo.proto.todo.v1.UpdateTodoRequest.class,
      responseType = monorepo.proto.todo.v1.Todo.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<monorepo.proto.todo.v1.UpdateTodoRequest,
      monorepo.proto.todo.v1.Todo> getUpdateTodoMethod() {
    io.grpc.MethodDescriptor<monorepo.proto.todo.v1.UpdateTodoRequest, monorepo.proto.todo.v1.Todo> getUpdateTodoMethod;
    if ((getUpdateTodoMethod = TodoServiceGrpc.getUpdateTodoMethod) == null) {
      synchronized (TodoServiceGrpc.class) {
        if ((getUpdateTodoMethod = TodoServiceGrpc.getUpdateTodoMethod) == null) {
          TodoServiceGrpc.getUpdateTodoMethod = getUpdateTodoMethod =
              io.grpc.MethodDescriptor.<monorepo.proto.todo.v1.UpdateTodoRequest, monorepo.proto.todo.v1.Todo>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateTodo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  monorepo.proto.todo.v1.UpdateTodoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  monorepo.proto.todo.v1.Todo.getDefaultInstance()))
              .setSchemaDescriptor(new TodoServiceMethodDescriptorSupplier("UpdateTodo"))
              .build();
        }
      }
    }
    return getUpdateTodoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<monorepo.proto.todo.v1.DeleteTodoRequest,
      monorepo.proto.todo.v1.Todo> getDeleteTodoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteTodo",
      requestType = monorepo.proto.todo.v1.DeleteTodoRequest.class,
      responseType = monorepo.proto.todo.v1.Todo.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<monorepo.proto.todo.v1.DeleteTodoRequest,
      monorepo.proto.todo.v1.Todo> getDeleteTodoMethod() {
    io.grpc.MethodDescriptor<monorepo.proto.todo.v1.DeleteTodoRequest, monorepo.proto.todo.v1.Todo> getDeleteTodoMethod;
    if ((getDeleteTodoMethod = TodoServiceGrpc.getDeleteTodoMethod) == null) {
      synchronized (TodoServiceGrpc.class) {
        if ((getDeleteTodoMethod = TodoServiceGrpc.getDeleteTodoMethod) == null) {
          TodoServiceGrpc.getDeleteTodoMethod = getDeleteTodoMethod =
              io.grpc.MethodDescriptor.<monorepo.proto.todo.v1.DeleteTodoRequest, monorepo.proto.todo.v1.Todo>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteTodo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  monorepo.proto.todo.v1.DeleteTodoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  monorepo.proto.todo.v1.Todo.getDefaultInstance()))
              .setSchemaDescriptor(new TodoServiceMethodDescriptorSupplier("DeleteTodo"))
              .build();
        }
      }
    }
    return getDeleteTodoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<monorepo.proto.todo.v1.BatchGetTodosRequest,
      monorepo.proto.todo.v1.BatchGetTodosResponse> getBatchGetTodosMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "BatchGetTodos",
      requestType = monorepo.proto.todo.v1.BatchGetTodosRequest.class,
      responseType = monorepo.proto.todo.v1.BatchGetTodosResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<monorepo.proto.todo.v1.BatchGetTodosRequest,
      monorepo.proto.todo.v1.BatchGetTodosResponse> getBatchGetTodosMethod() {
    io.grpc.MethodDescriptor<monorepo.proto.todo.v1.BatchGetTodosRequest, monorepo.proto.todo.v1.BatchGetTodosResponse> getBatchGetTodosMethod;
    if ((getBatchGetTodosMethod = TodoServiceGrpc.getBatchGetTodosMethod) == null) {
      synchronized (TodoServiceGrpc.class) {
        if ((getBatchGetTodosMethod = TodoServiceGrpc.getBatchGetTodosMethod) == null) {
          TodoServiceGrpc.getBatchGetTodosMethod = getBatchGetTodosMethod =
              io.grpc.MethodDescriptor.<monorepo.proto.todo.v1.BatchGetTodosRequest, monorepo.proto.todo.v1.BatchGetTodosResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "BatchGetTodos"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  monorepo.proto.todo.v1.BatchGetTodosRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  monorepo.proto.todo.v1.BatchGetTodosResponse.getDefaultInstance()))
              .setSchemaDescriptor(new TodoServiceMethodDescriptorSupplier("BatchGetTodos"))
              .build();
        }
      }
    }
    return getBatchGetTodosMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TodoServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TodoServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TodoServiceStub>() {
        @java.lang.Override
        public TodoServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TodoServiceStub(channel, callOptions);
        }
      };
    return TodoServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static TodoServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TodoServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TodoServiceBlockingV2Stub>() {
        @java.lang.Override
        public TodoServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TodoServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return TodoServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TodoServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TodoServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TodoServiceBlockingStub>() {
        @java.lang.Override
        public TodoServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TodoServiceBlockingStub(channel, callOptions);
        }
      };
    return TodoServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TodoServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TodoServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TodoServiceFutureStub>() {
        @java.lang.Override
        public TodoServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TodoServiceFutureStub(channel, callOptions);
        }
      };
    return TodoServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * TodoService provides CRUD operations for todos following AIP standards
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Create a new todo
     * </pre>
     */
    default void createTodo(monorepo.proto.todo.v1.CreateTodoRequest request,
        io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.Todo> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateTodoMethod(), responseObserver);
    }

    /**
     * <pre>
     * Get a todo by ID (including soft-deleted), returns NOT_FOUND if not exists
     * </pre>
     */
    default void getTodo(monorepo.proto.todo.v1.GetTodoRequest request,
        io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.Todo> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetTodoMethod(), responseObserver);
    }

    /**
     * <pre>
     * List todos with pagination, filtering, and sorting
     * </pre>
     */
    default void listTodos(monorepo.proto.todo.v1.ListTodosRequest request,
        io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.ListTodosResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getListTodosMethod(), responseObserver);
    }

    /**
     * <pre>
     * Update an existing todo
     * </pre>
     */
    default void updateTodo(monorepo.proto.todo.v1.UpdateTodoRequest request,
        io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.Todo> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateTodoMethod(), responseObserver);
    }

    /**
     * <pre>
     * Delete a todo by ID (soft delete)
     * </pre>
     */
    default void deleteTodo(monorepo.proto.todo.v1.DeleteTodoRequest request,
        io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.Todo> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteTodoMethod(), responseObserver);
    }

    /**
     * <pre>
     * Batch get todos by IDs (including soft-deleted)
     * </pre>
     */
    default void batchGetTodos(monorepo.proto.todo.v1.BatchGetTodosRequest request,
        io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.BatchGetTodosResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getBatchGetTodosMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service TodoService.
   * <pre>
   * TodoService provides CRUD operations for todos following AIP standards
   * </pre>
   */
  public static abstract class TodoServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return TodoServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service TodoService.
   * <pre>
   * TodoService provides CRUD operations for todos following AIP standards
   * </pre>
   */
  public static final class TodoServiceStub
      extends io.grpc.stub.AbstractAsyncStub<TodoServiceStub> {
    private TodoServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TodoServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TodoServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Create a new todo
     * </pre>
     */
    public void createTodo(monorepo.proto.todo.v1.CreateTodoRequest request,
        io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.Todo> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateTodoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Get a todo by ID (including soft-deleted), returns NOT_FOUND if not exists
     * </pre>
     */
    public void getTodo(monorepo.proto.todo.v1.GetTodoRequest request,
        io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.Todo> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetTodoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * List todos with pagination, filtering, and sorting
     * </pre>
     */
    public void listTodos(monorepo.proto.todo.v1.ListTodosRequest request,
        io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.ListTodosResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getListTodosMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Update an existing todo
     * </pre>
     */
    public void updateTodo(monorepo.proto.todo.v1.UpdateTodoRequest request,
        io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.Todo> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateTodoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Delete a todo by ID (soft delete)
     * </pre>
     */
    public void deleteTodo(monorepo.proto.todo.v1.DeleteTodoRequest request,
        io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.Todo> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteTodoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Batch get todos by IDs (including soft-deleted)
     * </pre>
     */
    public void batchGetTodos(monorepo.proto.todo.v1.BatchGetTodosRequest request,
        io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.BatchGetTodosResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getBatchGetTodosMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service TodoService.
   * <pre>
   * TodoService provides CRUD operations for todos following AIP standards
   * </pre>
   */
  public static final class TodoServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<TodoServiceBlockingV2Stub> {
    private TodoServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TodoServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TodoServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * Create a new todo
     * </pre>
     */
    public monorepo.proto.todo.v1.Todo createTodo(monorepo.proto.todo.v1.CreateTodoRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getCreateTodoMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Get a todo by ID (including soft-deleted), returns NOT_FOUND if not exists
     * </pre>
     */
    public monorepo.proto.todo.v1.Todo getTodo(monorepo.proto.todo.v1.GetTodoRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getGetTodoMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * List todos with pagination, filtering, and sorting
     * </pre>
     */
    public monorepo.proto.todo.v1.ListTodosResponse listTodos(monorepo.proto.todo.v1.ListTodosRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getListTodosMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Update an existing todo
     * </pre>
     */
    public monorepo.proto.todo.v1.Todo updateTodo(monorepo.proto.todo.v1.UpdateTodoRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getUpdateTodoMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Delete a todo by ID (soft delete)
     * </pre>
     */
    public monorepo.proto.todo.v1.Todo deleteTodo(monorepo.proto.todo.v1.DeleteTodoRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getDeleteTodoMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Batch get todos by IDs (including soft-deleted)
     * </pre>
     */
    public monorepo.proto.todo.v1.BatchGetTodosResponse batchGetTodos(monorepo.proto.todo.v1.BatchGetTodosRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getBatchGetTodosMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service TodoService.
   * <pre>
   * TodoService provides CRUD operations for todos following AIP standards
   * </pre>
   */
  public static final class TodoServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<TodoServiceBlockingStub> {
    private TodoServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TodoServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TodoServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Create a new todo
     * </pre>
     */
    public monorepo.proto.todo.v1.Todo createTodo(monorepo.proto.todo.v1.CreateTodoRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateTodoMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Get a todo by ID (including soft-deleted), returns NOT_FOUND if not exists
     * </pre>
     */
    public monorepo.proto.todo.v1.Todo getTodo(monorepo.proto.todo.v1.GetTodoRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetTodoMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * List todos with pagination, filtering, and sorting
     * </pre>
     */
    public monorepo.proto.todo.v1.ListTodosResponse listTodos(monorepo.proto.todo.v1.ListTodosRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getListTodosMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Update an existing todo
     * </pre>
     */
    public monorepo.proto.todo.v1.Todo updateTodo(monorepo.proto.todo.v1.UpdateTodoRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateTodoMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Delete a todo by ID (soft delete)
     * </pre>
     */
    public monorepo.proto.todo.v1.Todo deleteTodo(monorepo.proto.todo.v1.DeleteTodoRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteTodoMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Batch get todos by IDs (including soft-deleted)
     * </pre>
     */
    public monorepo.proto.todo.v1.BatchGetTodosResponse batchGetTodos(monorepo.proto.todo.v1.BatchGetTodosRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getBatchGetTodosMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service TodoService.
   * <pre>
   * TodoService provides CRUD operations for todos following AIP standards
   * </pre>
   */
  public static final class TodoServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<TodoServiceFutureStub> {
    private TodoServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TodoServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TodoServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Create a new todo
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<monorepo.proto.todo.v1.Todo> createTodo(
        monorepo.proto.todo.v1.CreateTodoRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateTodoMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Get a todo by ID (including soft-deleted), returns NOT_FOUND if not exists
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<monorepo.proto.todo.v1.Todo> getTodo(
        monorepo.proto.todo.v1.GetTodoRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetTodoMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * List todos with pagination, filtering, and sorting
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<monorepo.proto.todo.v1.ListTodosResponse> listTodos(
        monorepo.proto.todo.v1.ListTodosRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getListTodosMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Update an existing todo
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<monorepo.proto.todo.v1.Todo> updateTodo(
        monorepo.proto.todo.v1.UpdateTodoRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateTodoMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Delete a todo by ID (soft delete)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<monorepo.proto.todo.v1.Todo> deleteTodo(
        monorepo.proto.todo.v1.DeleteTodoRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteTodoMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Batch get todos by IDs (including soft-deleted)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<monorepo.proto.todo.v1.BatchGetTodosResponse> batchGetTodos(
        monorepo.proto.todo.v1.BatchGetTodosRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getBatchGetTodosMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_TODO = 0;
  private static final int METHODID_GET_TODO = 1;
  private static final int METHODID_LIST_TODOS = 2;
  private static final int METHODID_UPDATE_TODO = 3;
  private static final int METHODID_DELETE_TODO = 4;
  private static final int METHODID_BATCH_GET_TODOS = 5;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CREATE_TODO:
          serviceImpl.createTodo((monorepo.proto.todo.v1.CreateTodoRequest) request,
              (io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.Todo>) responseObserver);
          break;
        case METHODID_GET_TODO:
          serviceImpl.getTodo((monorepo.proto.todo.v1.GetTodoRequest) request,
              (io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.Todo>) responseObserver);
          break;
        case METHODID_LIST_TODOS:
          serviceImpl.listTodos((monorepo.proto.todo.v1.ListTodosRequest) request,
              (io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.ListTodosResponse>) responseObserver);
          break;
        case METHODID_UPDATE_TODO:
          serviceImpl.updateTodo((monorepo.proto.todo.v1.UpdateTodoRequest) request,
              (io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.Todo>) responseObserver);
          break;
        case METHODID_DELETE_TODO:
          serviceImpl.deleteTodo((monorepo.proto.todo.v1.DeleteTodoRequest) request,
              (io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.Todo>) responseObserver);
          break;
        case METHODID_BATCH_GET_TODOS:
          serviceImpl.batchGetTodos((monorepo.proto.todo.v1.BatchGetTodosRequest) request,
              (io.grpc.stub.StreamObserver<monorepo.proto.todo.v1.BatchGetTodosResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getCreateTodoMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              monorepo.proto.todo.v1.CreateTodoRequest,
              monorepo.proto.todo.v1.Todo>(
                service, METHODID_CREATE_TODO)))
        .addMethod(
          getGetTodoMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              monorepo.proto.todo.v1.GetTodoRequest,
              monorepo.proto.todo.v1.Todo>(
                service, METHODID_GET_TODO)))
        .addMethod(
          getListTodosMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              monorepo.proto.todo.v1.ListTodosRequest,
              monorepo.proto.todo.v1.ListTodosResponse>(
                service, METHODID_LIST_TODOS)))
        .addMethod(
          getUpdateTodoMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              monorepo.proto.todo.v1.UpdateTodoRequest,
              monorepo.proto.todo.v1.Todo>(
                service, METHODID_UPDATE_TODO)))
        .addMethod(
          getDeleteTodoMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              monorepo.proto.todo.v1.DeleteTodoRequest,
              monorepo.proto.todo.v1.Todo>(
                service, METHODID_DELETE_TODO)))
        .addMethod(
          getBatchGetTodosMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              monorepo.proto.todo.v1.BatchGetTodosRequest,
              monorepo.proto.todo.v1.BatchGetTodosResponse>(
                service, METHODID_BATCH_GET_TODOS)))
        .build();
  }

  private static abstract class TodoServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TodoServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return monorepo.proto.todo.v1.TodoServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("TodoService");
    }
  }

  private static final class TodoServiceFileDescriptorSupplier
      extends TodoServiceBaseDescriptorSupplier {
    TodoServiceFileDescriptorSupplier() {}
  }

  private static final class TodoServiceMethodDescriptorSupplier
      extends TodoServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    TodoServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (TodoServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TodoServiceFileDescriptorSupplier())
              .addMethod(getCreateTodoMethod())
              .addMethod(getGetTodoMethod())
              .addMethod(getListTodosMethod())
              .addMethod(getUpdateTodoMethod())
              .addMethod(getDeleteTodoMethod())
              .addMethod(getBatchGetTodosMethod())
              .build();
        }
      }
    }
    return result;
  }
}
