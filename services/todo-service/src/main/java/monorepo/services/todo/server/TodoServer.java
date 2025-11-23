package monorepo.services.todo.server;

import grpcstarter.server.GrpcService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import monorepo.proto.todo.v1.BatchGetTodosRequest;
import monorepo.proto.todo.v1.BatchGetTodosResponse;
import monorepo.proto.todo.v1.CreateTodoRequest;
import monorepo.proto.todo.v1.DeleteTodoRequest;
import monorepo.proto.todo.v1.GetTodoRequest;
import monorepo.proto.todo.v1.ListTodosRequest;
import monorepo.proto.todo.v1.ListTodosResponse;
import monorepo.proto.todo.v1.TodoModel;
import monorepo.proto.todo.v1.TodoServiceGrpc;
import monorepo.proto.todo.v1.UpdateTodoRequest;
import monorepo.services.todo.service.TodoService;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/23
 */
@GrpcService
@RequiredArgsConstructor
public class TodoServer extends TodoServiceGrpc.TodoServiceImplBase {

    private final TodoService todoService;

    @Override
    public void createTodo(CreateTodoRequest request, StreamObserver<TodoModel> responseObserver) {
        var todoId = todoService.create(request);
        var todo = todoService.get(GetTodoRequest.newBuilder().setId(todoId).build());
        responseObserver.onNext(todo);
        responseObserver.onCompleted();
    }

    @Override
    public void getTodo(GetTodoRequest request, StreamObserver<TodoModel> responseObserver) {
        var todo = todoService.get(request);
        responseObserver.onNext(todo);
        responseObserver.onCompleted();
    }

    @Override
    public void listTodos(ListTodosRequest request, StreamObserver<ListTodosResponse> responseObserver) {

        //        responseObserver.onNext(response);
        //        responseObserver.onCompleted();
    }

    @Override
    public void updateTodo(UpdateTodoRequest request, StreamObserver<TodoModel> responseObserver) {
        todoService.update(request);
        var todo = todoService.get(
                GetTodoRequest.newBuilder().setId(request.getTodo().getId()).build());
        responseObserver.onNext(todo);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteTodo(DeleteTodoRequest request, StreamObserver<TodoModel> responseObserver) {
        todoService.delete(request);
        var todo = todoService.get(
                GetTodoRequest.newBuilder().setId(request.getId()).build());
        responseObserver.onNext(todo);
        responseObserver.onCompleted();
    }

    @Override
    public void batchGetTodos(BatchGetTodosRequest request, StreamObserver<BatchGetTodosResponse> responseObserver) {
        var todos = todoService.batchGet(request);
        var response = BatchGetTodosResponse.newBuilder().addAllTodos(todos).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
