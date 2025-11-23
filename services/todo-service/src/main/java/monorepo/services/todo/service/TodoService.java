package monorepo.services.todo.service;

import static monorepo.lib.common.util.SpringUtil.withTransaction;
import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.todo;
import static monorepo.services.todo.mapper.TodoSubtaskDynamicSqlSupport.todoSubtask;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;
import static org.mybatis.dynamic.sql.SqlBuilder.isNull;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import monorepo.proto.todo.v1.BatchGetTodosRequest;
import monorepo.proto.todo.v1.CreateTodoRequest;
import monorepo.proto.todo.v1.DeleteTodoRequest;
import monorepo.proto.todo.v1.GetTodoRequest;
import monorepo.proto.todo.v1.TodoModel;
import monorepo.proto.todo.v1.UpdateTodoRequest;
import monorepo.services.todo.converter.TodoConverter;
import monorepo.services.todo.entity.TodoSubtask;
import monorepo.services.todo.mapper.TodoMapper;
import monorepo.services.todo.mapper.TodoSubtaskMapper;
import org.jspecify.annotations.Nullable;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoMapper todoMapper;
    private final TodoSubtaskMapper todoSubtaskMapper;

    /**
     * Create a new todo, return the created todo id.
     *
     * @param request create todo request
     * @return created todo id
     */
    public long create(CreateTodoRequest request) {
        return withTransaction(() -> {
            var todoId = createTodo(request.getTodo());

            for (var subtaskRequest : request.getSubTasksList()) {
                createTodoSubtask(subtaskRequest, todoId);
            }

            return todoId;
        });
    }

    /**
     * Update an existing todo.
     *
     * @param request update todo request
     * @return whether update succeeded
     */
    public boolean update(UpdateTodoRequest request) {
        return withTransaction(() -> {
            var result = updateTodo(request.getTodo());

            var todoId = request.getTodo().getId();
            for (var subtask : request.getSubTasksList()) {
                switch (subtask.getOperationCase()) {
                    case CREATE -> createTodoSubtask(subtask.getCreate(), todoId);
                    case UPDATE -> updateTodoSubtask(subtask.getUpdate(), todoId);
                    case DELETE -> deleteTodoSubtask(subtask.getDelete(), todoId);
                    case OPERATION_NOT_SET -> {}
                }
            }

            return result;
        });
    }

    /**
     * Delete an existing todo.
     *
     * @param request delete todo request
     * @return whether delete succeeded
     */
    public boolean delete(DeleteTodoRequest request) {
        return deleteTodo(request.getId());
    }

    /**
     * Get a todo by id, return null if not found.
     *
     * @param request get todo request
     * @return the todo model or null if not found
     */
    @Nullable
    public TodoModel getOrNull(GetTodoRequest request) {
        var todos = batchGet(toBatchGetTodosRequest(request));
        if (todos.isEmpty()) {
            return null;
        }
        return todos.getFirst();
    }

    /**
     * Get a todo by id, throw NOT_FOUND if not found.
     *
     * @param request get todo request
     * @return the todo model
     */
    public TodoModel get(GetTodoRequest request) {
        var todo = getOrNull(request);
        if (todo == null) {
            throw new StatusRuntimeException(
                    Status.NOT_FOUND.withDescription("Todo not found, id: " + request.getId()));
        }
        return todo;
    }

    /**
     * Batch get todos by ids.
     *
     * @param request batch get todos request
     * @return list of todo models
     */
    public List<TodoModel> batchGet(BatchGetTodosRequest request) {
        var ids = request.getIdsList();
        if (ids.isEmpty()) {
            return List.of();
        }

        SelectDSLCompleter dsl = c -> {
            var sql = c.where(todo.id, isIn(ids));
            var showDeleted = !request.hasShowDeleted() || request.getShowDeleted();
            if (!showDeleted) {
                sql.and(todo.deletedAt, isNull());
            }
            return sql;
        };
        var todos = todoMapper.select(dsl);
        if (todos.isEmpty()) {
            return List.of();
        }

        var todoIdToTodoSubtasks =
                todoSubtaskMapper
                        .select(c -> c.where(todoSubtask.todoId, isIn(ids)).and(todoSubtask.deletedAt, isNull()))
                        .stream()
                        .collect(Collectors.groupingBy(TodoSubtask::getTodoId));

        var result = new ArrayList<TodoModel>();
        for (var entity : todos) {
            var builder = TodoModel.newBuilder();
            var todo = TodoConverter.INSTANCE.toTodoModel(entity);
            var subtasks = todoIdToTodoSubtasks.getOrDefault(entity.getId(), List.of()).stream()
                    .map(TodoConverter.INSTANCE::toTodoSubtaskModel)
                    .toList();
            builder.setTodo(todo);
            builder.addAllSubTasks(subtasks);
            result.add(builder.build());
        }
        return result;
    }

    private long createTodo(CreateTodoRequest.Todo request) {
        var todo = TodoConverter.INSTANCE.toTodo(request);
        todoMapper.insertSelective(todo);
        return todo.getId();
    }

    private boolean updateTodo(UpdateTodoRequest.Todo request) {
        var entity = TodoConverter.INSTANCE.toTodo(request);
        if (entity.getUpdatedAt() == null) {
            entity.setUpdatedAt(LocalDateTime.now());
        }
        return todoMapper.update(c -> TodoMapper.updateSelectiveColumns(entity, c)
                        .where(todo.id, isEqualTo(request.getId()))
                        .and(todo.deletedAt, isNull()))
                > 0;
    }

    private boolean deleteTodo(long id) {
        return todoMapper.update(c -> c.set(todo.deletedAt)
                        .equalTo(LocalDateTime.now())
                        .where(todo.id, isEqualTo(id))
                        .and(todo.deletedAt, isNull()))
                > 0;
    }

    private long createTodoSubtask(CreateTodoRequest.SubTask request, long todoId) {
        var subtask = TodoConverter.INSTANCE.toTodoSubtask(request);
        subtask.setTodoId(todoId);
        todoSubtaskMapper.insertSelective(subtask);
        return subtask.getId();
    }

    private boolean updateTodoSubtask(UpdateTodoRequest.SubTask.Update request, long todoId) {
        var subtask = TodoConverter.INSTANCE.toTodoSubtask(request);
        return todoSubtaskMapper.update(c -> TodoSubtaskMapper.updateSelectiveColumns(subtask, c)
                        .where(todoSubtask.id, isEqualTo(request.getId()))
                        .and(todoSubtask.todoId, isEqualTo(todoId))
                        .and(todoSubtask.deletedAt, isNull()))
                > 0;
    }

    private boolean deleteTodoSubtask(long id, long todoId) {
        return todoSubtaskMapper.update(c -> c.set(todoSubtask.deletedAt)
                        .equalTo(LocalDateTime.now())
                        .where(todoSubtask.id, isEqualTo(id))
                        .and(todoSubtask.todoId, isEqualTo(todoId))
                        .and(todoSubtask.deletedAt, isNull()))
                > 0;
    }

    private static BatchGetTodosRequest toBatchGetTodosRequest(GetTodoRequest request) {
        var builder = BatchGetTodosRequest.newBuilder();
        builder.addIds(request.getId());
        if (request.hasShowDeleted()) {
            builder.setShowDeleted(request.getShowDeleted());
        } else {
            builder.setShowDeleted(true);
        }
        return builder.build();
    }
}
