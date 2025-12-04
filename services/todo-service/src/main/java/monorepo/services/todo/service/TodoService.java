package monorepo.services.todo.service;

import static monorepo.lib.common.util.SpringUtil.withTransaction;
import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.todo;
import static monorepo.services.todo.mapper.TodoSubtaskDynamicSqlSupport.todoSubtask;
import static org.mybatis.dynamic.sql.SqlBuilder.and;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThan;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;
import static org.mybatis.dynamic.sql.SqlBuilder.isInWhenPresent;
import static org.mybatis.dynamic.sql.SqlBuilder.isNull;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import monorepo.lib.common.pagination.PageTokenState;
import monorepo.proto.todo.v1.BatchGetTodosRequest;
import monorepo.proto.todo.v1.CreateSubtaskRequest;
import monorepo.proto.todo.v1.CreateTodoRequest;
import monorepo.proto.todo.v1.DeleteSubtaskRequest;
import monorepo.proto.todo.v1.DeleteTodoRequest;
import monorepo.proto.todo.v1.GetTodoRequest;
import monorepo.proto.todo.v1.ListTodosRequest;
import monorepo.proto.todo.v1.ListTodosResponse;
import monorepo.proto.todo.v1.UpdateSubtaskRequest;
import monorepo.proto.todo.v1.UpdateTodoRequest;
import monorepo.services.todo.converter.TodoConverter;
import monorepo.services.todo.entity.Todo;
import monorepo.services.todo.entity.TodoSubtask;
import monorepo.services.todo.mapper.TodoMapper;
import monorepo.services.todo.mapper.TodoSubtaskMapper;
import org.jspecify.annotations.Nullable;
import org.mybatis.dynamic.sql.AndOrCriteriaGroup;
import org.mybatis.dynamic.sql.SortSpecification;
import org.mybatis.dynamic.sql.SqlColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoService.class);
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
            var todoId = createTodo(request);

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
            var result = updateTodo(request);

            var todoId = request.getId();
            for (var subtask : request.getSubTaskOperationsList()) {
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
    public monorepo.proto.todo.v1.@Nullable Todo getOrNull(GetTodoRequest request) {
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
    public monorepo.proto.todo.v1.Todo get(GetTodoRequest request) {
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
     * @return list of todo
     */
    public List<monorepo.proto.todo.v1.Todo> batchGet(BatchGetTodosRequest request) {
        var ids = request.getIdsList();
        if (ids.isEmpty()) {
            return List.of();
        }

        var entities = todoMapper.select(c -> {
            var sql = c.where(todo.id, isIn(ids));
            var showDeleted = !request.hasShowDeleted() || request.getShowDeleted();
            if (!showDeleted) {
                sql.and(todo.deletedAt, isNull());
            }
            return sql;
        });

        return buildTodos(entities);
    }

    /**
     * List todos with pagination and filtering.
     *
     * @param request list todos request
     * @return list todos response
     */
    public ListTodosResponse list(ListTodosRequest request) {
        int pageSize;
        if (request.getPageSize() <= 0) {
            pageSize = 100;
        } else if (request.getPageSize() > 1000) {
            pageSize = 1000;
        } else {
            pageSize = request.getPageSize();
        }

        var pageTokenState = fromPageToken(request);
        if (pageTokenState == null && !request.getPageToken().isEmpty()) {
            throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("Invalid page token"));
        }

        // Query total count
        var totalSize = todoMapper.count(c -> c.where(buildConditions(request)));
        if (totalSize == 0) {
            return ListTodosResponse.newBuilder().setTotalSize(0).build();
        }

        // Build query with cursor-based pagination
        var entities = todoMapper.select(c -> {
            var conditions = buildConditions(request);

            // Add cursor condition if we have a valid page token
            if (pageTokenState != null && pageTokenState.lastId() != null) {
                conditions.add(and(todo.id, isGreaterThan(Long.parseLong(pageTokenState.lastId()))));
            }

            var sql = c.where(conditions);

            // Apply ordering
            var orderBys = new ArrayList<SortSpecification>();
            for (var orderBy : request.getOrderByList()) {
                var column = mapFieldToColumn(orderBy.getField());
                if (column != null) {
                    if (orderBy.getIsDesc()) orderBys.add(column.descending());
                    else orderBys.add(column);
                }
            }
            orderBys.add(todo.id);

            return sql.orderBy(orderBys).limit(pageSize);
        });
        if (entities.isEmpty()) {
            return ListTodosResponse.newBuilder().setTotalSize((int) totalSize).build();
        }

        var todos = buildTodos(entities);

        // Build response with next page token
        var builder = ListTodosResponse.newBuilder();
        builder.addAllTodos(todos);
        builder.setTotalSize((int) totalSize);

        // Generate next page token if there are more results
        if (todos.size() == pageSize) {
            var last = entities.getLast();
            var newOffset = (pageTokenState != null ? pageTokenState.offset() : 0) + pageSize;

            var newPageTokenState = new PageTokenState(
                    String.valueOf(last.getId()), newOffset, calculateFilterHash(request), calculateSortHash(request));
            builder.setNextPageToken(newPageTokenState.toPageToken());
        }

        return builder.build();
    }

    @Nullable
    private static PageTokenState fromPageToken(ListTodosRequest request) {
        var pageToken = request.getPageToken();
        if (pageToken.isEmpty()) {
            return null;
        }

        PageTokenState result;
        try {
            result = PageTokenState.fromPageToken(pageToken);
        } catch (Exception e) {
            log.warn("Invalid page token format: {}", pageToken);
            return null;
        }

        var currentFilterHash = calculateFilterHash(request);
        if (!Objects.equals(result.filterHash(), currentFilterHash)) {
            log.warn("Page token filter mismatch, ignoring token");
            return null;
        }

        var currentSortHash = calculateSortHash(request);
        if (!Objects.equals(result.sortHash(), currentSortHash)) {
            log.warn("Page token sort mismatch, ignoring token");
            return null;
        }

        return result;
    }

    private List<monorepo.proto.todo.v1.Todo> buildTodos(List<Todo> todos) {
        if (todos.isEmpty()) {
            return List.of();
        }

        var todoIds = todos.stream().map(Todo::getId).toList();
        var todoIdToTodoSubtasks =
                todoSubtaskMapper
                        .select(c -> c.where(todoSubtask.todoId, isIn(todoIds)).and(todoSubtask.deletedAt, isNull()))
                        .stream()
                        .collect(Collectors.groupingBy(TodoSubtask::getTodoId));

        var result = new ArrayList<monorepo.proto.todo.v1.Todo>(todos.size());

        for (var todoEntity : todos) {
            var subtasks = todoIdToTodoSubtasks.getOrDefault(todoEntity.getId(), List.of());
            var todo = TodoConverter.INSTANCE.buildTodo(todoEntity, subtasks);
            result.add(todo);
        }

        return result;
    }

    private static List<AndOrCriteriaGroup> buildConditions(ListTodosRequest request) {
        var result = new ArrayList<AndOrCriteriaGroup>();
        result.add(and(todo.userId, isEqualTo(request.getUserId())));
        if (!request.getShowDeleted()) {
            result.add(and(todo.deletedAt, isNull()));
        }
        if (request.hasFilter()) {
            var filter = request.getFilter();
            result.add(and(todo.state, isInWhenPresent(filter.getStatesList())));
            result.add(and(todo.priority, isInWhenPresent(filter.getPrioritiesList())));
        }
        return result;
    }

    @Nullable
    private static SqlColumn<?> mapFieldToColumn(String field) {
        return switch (field) {
            case "created_at" -> todo.createdAt;
            case "updated_at" -> todo.updatedAt;
            case "due_date" -> todo.dueDate;
            case "priority" -> todo.priority;
            case "state" -> todo.state;
            case "title" -> todo.title;
            default -> {
                log.warn("Unknown order by field: {}", field);
                yield null;
            }
        };
    }

    private static String calculateFilterHash(ListTodosRequest request) {
        var builder = ListTodosRequest.newBuilder();
        builder.setUserId(request.getUserId());
        if (request.hasFilter()) {
            builder.setFilter(request.getFilter());
        }
        if (request.hasShowDeleted()) {
            builder.setShowDeleted(request.getShowDeleted());
        }
        return String.valueOf(Objects.hashCode(builder.build()));
    }

    private static String calculateSortHash(ListTodosRequest request) {
        return String.valueOf(Objects.hashCode(request.getOrderByList()));
    }

    private long createTodo(CreateTodoRequest request) {
        var todo = TodoConverter.INSTANCE.toTodoEntity(request);
        todoMapper.insertSelective(todo);
        return todo.getId();
    }

    private boolean updateTodo(UpdateTodoRequest request) {
        var entity = TodoConverter.INSTANCE.toTodoEntity(request);
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

    private long createTodoSubtask(CreateSubtaskRequest request, long todoId) {
        var subtask = TodoConverter.INSTANCE.toTodoSubtaskEntity(request);
        subtask.setTodoId(todoId);
        todoSubtaskMapper.insertSelective(subtask);
        return subtask.getId();
    }

    private boolean updateTodoSubtask(UpdateSubtaskRequest request, long todoId) {
        var subtask = TodoConverter.INSTANCE.toTodoSubtaskEntity(request);
        return todoSubtaskMapper.update(c -> TodoSubtaskMapper.updateSelectiveColumns(subtask, c)
                        .where(todoSubtask.id, isEqualTo(request.getId()))
                        .and(todoSubtask.todoId, isEqualTo(todoId))
                        .and(todoSubtask.deletedAt, isNull()))
                > 0;
    }

    private boolean deleteTodoSubtask(DeleteSubtaskRequest request, long todoId) {
        return todoSubtaskMapper.update(c -> c.set(todoSubtask.deletedAt)
                        .equalTo(LocalDateTime.now())
                        .where(todoSubtask.id, isEqualTo(request.getId()))
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
