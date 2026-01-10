package monorepo.services.todo.service;

import static monorepo.lib.common.util.SpringUtil.withTransaction;
import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.todo;
import static monorepo.services.todo.mapper.TodoSubtaskDynamicSqlSupport.todoSubtask;
import static org.mybatis.dynamic.sql.SqlBuilder.and;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThan;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;
import static org.mybatis.dynamic.sql.SqlBuilder.isInWhenPresent;
import static org.mybatis.dynamic.sql.SqlBuilder.isLessThan;
import static org.mybatis.dynamic.sql.SqlBuilder.isNull;
import static org.mybatis.dynamic.sql.SqlBuilder.or;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        return deleteTodo(request);
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
        var result = todos.getFirst();
        if (request.hasUserId() && result.getUserId() != request.getUserId()) {
            return null;
        }
        return result;
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

        var entities = todoMapper.useDataSource("reader").select(c -> {
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
        // Count total matching todos
        var total = countTodos(request);
        if (total == 0) {
            return ListTodosResponse.getDefaultInstance();
        }

        // Get todos with pagination
        var entities = getTodoEntities(request);
        if (entities.isEmpty()) {
            return ListTodosResponse.getDefaultInstance();
        }

        var todos = buildTodos(entities);

        var builder = ListTodosResponse.newBuilder();
        builder.addAllTodos(todos);
        builder.setTotalSize((int) total);

        // Generate next page token if there are more results
        if (todos.size() == normalizePageSize(request.getPageSize())) {
            builder.setNextPageToken(generatePageToken(request, entities.getLast()));
        }

        return builder.build();
    }

    private long countTodos(ListTodosRequest request) {
        return todoMapper.count(c -> c.where(buildConditions(request)));
    }

    private List<Todo> getTodoEntities(ListTodosRequest request) {
        // Build query with cursor-based pagination
        return todoMapper.select(c -> {
            // where ...
            var conditions = new ArrayList<>(buildConditions(request));

            var pageTokenState = fromPageToken(request);
            if (pageTokenState != null && !pageTokenState.lastValues().isEmpty()) {
                var cursorCondition = buildCursorCondition(request.getOrderByList(), pageTokenState.lastValues());
                if (!cursorCondition.isEmpty()) {
                    conditions.add(and(cursorCondition));
                }
            }

            // order by ...
            var orderBys = new ArrayList<SortSpecification>();
            for (var orderBy : request.getOrderByList()) {
                var column = getSqlColumn(orderBy.getField());
                if (orderBy.getIsDesc()) {
                    orderBys.add(column.descending());
                } else {
                    orderBys.add(column);
                }
            }
            orderBys.add(todo.id);

            return c.where(conditions).orderBy(orderBys).limit(normalizePageSize(request.getPageSize()));
        });
    }

    private static String generatePageToken(ListTodosRequest request, Todo entity) {
        var lastValues = extractFieldValues(entity, request.getOrderByList());
        var newPageTokenState =
                new PageTokenState(lastValues, calculateFilterHash(request), calculateSortHash(request));
        return newPageTokenState.toPageToken();
    }

    private static int normalizePageSize(int pageSize) {
        if (pageSize < 0) {
            throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("Page size must be non-negative"));
        } else if (pageSize == 0) {
            return 100; // default page size
        } else {
            return Math.min(pageSize, 1000);
        }
    }

    /**
     * Build cursor condition for multi-field pagination. For sort order [field1 DESC, field2 ASC, id
     * ASC] with last values {field1: v1, field2: v2, id: v3}, generates: (field1 < v1) OR (field1 =
     * v1 AND field2 > v2) OR (field1 = v1 AND field2 = v2 AND id > v3)
     */
    private static List<AndOrCriteriaGroup> buildCursorCondition(
            List<ListTodosRequest.OrderBy> orderBys, Map<String, String> lastValues) {
        var result = new ArrayList<AndOrCriteriaGroup>();

        // Build condition for each orderBy field
        for (int i = 0; i < orderBys.size(); i++) {
            var condition = buildOrderByFieldCondition(orderBys, lastValues, i);
            if (!condition.isEmpty()) {
                result.add(or(condition));
            }
        }

        // Build condition for stable sort field 'id'
        var idCondition = buildIdFieldCondition(orderBys, lastValues);
        if (!idCondition.isEmpty()) {
            result.add(or(idCondition));
        }

        return result;
    }

    /**
     * Build condition for a single orderBy field at the given index. Returns: (prefix conditions) AND
     * (current field comparison)
     */
    private static List<AndOrCriteriaGroup> buildOrderByFieldCondition(
            List<ListTodosRequest.OrderBy> orderBys, Map<String, String> lastValues, int currentIndex) {
        var orderBy = orderBys.get(currentIndex);
        var column = getSqlColumn(orderBy.getField());
        var lastValue = lastValues.get(column.name());
        if (lastValue == null) {
            return List.of();
        }

        var result = new ArrayList<AndOrCriteriaGroup>();

        // Build prefix: all previous fields must equal their last values
        var prefixConditions = buildPrefixEqualityConditions(orderBys, lastValues, 0, currentIndex);
        result.addAll(prefixConditions);

        // Build comparison for current field
        var comparison = buildFieldComparison(orderBy, lastValue);
        result.add(comparison);

        return result;
    }

    /**
     * Build condition for the stable sort field 'id'. Returns: (all orderBy fields equal) AND (id >
     * lastId)
     */
    private static List<AndOrCriteriaGroup> buildIdFieldCondition(
            List<ListTodosRequest.OrderBy> orderBys, Map<String, String> lastValues) {
        var idLastValue = lastValues.get(todo.id.name());
        if (idLastValue == null) {
            return List.of();
        }

        var result = new ArrayList<AndOrCriteriaGroup>();

        // All orderBy fields must equal their last values
        var prefixConditions = buildPrefixEqualityConditions(orderBys, lastValues, 0, orderBys.size());
        result.addAll(prefixConditions);

        // id is always sorted ASC, so use greater than
        var idComparison = and(todo.id, isGreaterThan(Long.parseLong(idLastValue)));
        result.add(idComparison);

        return result;
    }

    /** Build equality conditions for fields in range [startIndex, endIndex). */
    private static List<AndOrCriteriaGroup> buildPrefixEqualityConditions(
            List<ListTodosRequest.OrderBy> orderBys, Map<String, String> lastValues, int startIndex, int endIndex) {
        var conditions = new ArrayList<AndOrCriteriaGroup>();

        for (int i = startIndex; i < endIndex; i++) {
            var orderBy = orderBys.get(i);
            var column = getSqlColumn(orderBy.getField());
            var lastValue = lastValues.get(column.name());
            if (lastValue == null) {
                continue;
            }

            conditions.add(buildFieldEquality(orderBy, lastValue));
        }

        return conditions;
    }

    /** Build equality condition for a field. */
    private static AndOrCriteriaGroup buildFieldEquality(ListTodosRequest.OrderBy orderBy, String value) {
        var field = orderBy.getField();
        return switch (field) {
            case CREATED_AT -> and(todo.createdAt, isEqualTo(Instant.parse(value)));
            case DUE_DATE -> and(todo.dueDate, isEqualTo(LocalDate.parse(value)));
            case PRIORITY -> and(todo.priority, isEqualTo(monorepo.proto.todo.v1.Todo.Priority.valueOf(value)));
            case FIELD_UNSPECIFIED, UNRECOGNIZED ->
                throw new StatusRuntimeException(
                        Status.INVALID_ARGUMENT.withDescription("Invalid order by field: " + field));
        };
    }

    private static AndOrCriteriaGroup buildFieldComparison(ListTodosRequest.OrderBy orderBy, String value) {
        var field = orderBy.getField();
        var isDesc = orderBy.getIsDesc();
        return switch (field) {
            case CREATED_AT -> {
                var parsedValue = Instant.parse(value);
                yield isDesc
                        ? and(todo.createdAt, isLessThan(parsedValue))
                        : and(todo.createdAt, isGreaterThan(parsedValue));
            }
            case DUE_DATE -> {
                var parsedValue = LocalDate.parse(value);
                yield isDesc
                        ? and(todo.dueDate, isLessThan(parsedValue))
                        : and(todo.dueDate, isGreaterThan(parsedValue));
            }
            case PRIORITY -> {
                var parsedValue = monorepo.proto.todo.v1.Todo.Priority.valueOf(value);
                yield isDesc
                        ? and(todo.priority, isLessThan(parsedValue))
                        : and(todo.priority, isGreaterThan(parsedValue));
            }
            case FIELD_UNSPECIFIED, UNRECOGNIZED ->
                throw new StatusRuntimeException(
                        Status.INVALID_ARGUMENT.withDescription("Invalid order by field: " + field));
        };
    }

    /** Extract field values from entity for all sort fields. */
    private static Map<String, String> extractFieldValues(Todo entity, List<ListTodosRequest.OrderBy> orderBys) {
        var result = new HashMap<String, String>();

        for (var spec : orderBys) {
            switch (spec.getField()) {
                case CREATED_AT ->
                    result.put(todo.createdAt.name(), entity.getCreatedAt().toString());
                case DUE_DATE -> {
                    if (entity.getDueDate() != null) {
                        result.put(todo.dueDate.name(), entity.getDueDate().toString());
                    }
                }
                case PRIORITY ->
                    result.put(todo.priority.name(), entity.getPriority().name());
                case FIELD_UNSPECIFIED, UNRECOGNIZED -> {}
            }
        }

        // Always include id for stable sorting
        result.put(todo.id.name(), String.valueOf(entity.getId()));

        return result;
    }

    private static @Nullable PageTokenState fromPageToken(ListTodosRequest request) {
        var pageToken = request.getPageToken();
        if (pageToken.isBlank()) {
            return null;
        }

        PageTokenState result;
        try {
            result = PageTokenState.fromPageToken(pageToken);
        } catch (Exception e) {
            throw new StatusRuntimeException(Status.INVALID_ARGUMENT
                    .withDescription("Invalid page token format.")
                    .withCause(e));
        }

        var currentFilterHash = calculateFilterHash(request);
        if (!Objects.equals(result.filterHash(), currentFilterHash)) {
            throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(
                    "Page token filter changed. You must reset pagination by not providing a page token."));
        }

        var currentSortHash = calculateSortHash(request);
        if (!Objects.equals(result.sortHash(), currentSortHash)) {
            throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(
                    "Page token sortBy changed. You must reset pagination by not providing a page token."));
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

    private static SqlColumn<?> getSqlColumn(ListTodosRequest.OrderBy.Field field) {
        return switch (field) {
            case CREATED_AT -> todo.createdAt;
            case DUE_DATE -> todo.dueDate;
            case PRIORITY -> todo.priority;
            case FIELD_UNSPECIFIED, UNRECOGNIZED ->
                throw new StatusRuntimeException(
                        Status.INVALID_ARGUMENT.withDescription("Invalid order by field: " + field));
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
        if (todo.getCreatedAt() == null) {
            todo.setCreatedAt(Instant.now());
        }
        if (todo.getUpdatedAt() == null) {
            todo.setUpdatedAt(Instant.now());
        }
        todoMapper.insertSelective(todo);
        return todo.getId();
    }

    private boolean updateTodo(UpdateTodoRequest request) {
        var entity = TodoConverter.INSTANCE.toTodoEntity(request);
        if (entity.getUpdatedAt() == null) {
            entity.setUpdatedAt(Instant.now());
        }

        var id = request.getId();
        var userId = request.hasUserId() ? request.getUserId() : null;
        return todoMapper.update(c -> TodoMapper.updateSelectiveColumns(entity, c)
                        .where(todo.id, isEqualTo(id))
                        .and(todo.userId, isEqualToWhenPresent(userId))
                        .and(todo.deletedAt, isNull()))
                > 0;
    }

    private boolean deleteTodo(DeleteTodoRequest request) {
        var id = request.getId();
        var userId = request.hasUserId() ? request.getUserId() : null;
        return todoMapper.update(c -> c.set(todo.deletedAt)
                        .equalTo(Instant.now())
                        .where(todo.id, isEqualTo(id))
                        .and(todo.userId, isEqualToWhenPresent(userId))
                        .and(todo.deletedAt, isNull()))
                > 0;
    }

    private long createTodoSubtask(CreateSubtaskRequest request, long todoId) {
        var subtask = TodoConverter.INSTANCE.toTodoSubtaskEntity(request);
        subtask.setTodoId(todoId);
        if (subtask.getCreatedAt() == null) {
            subtask.setCreatedAt(Instant.now());
        }
        if (subtask.getUpdatedAt() == null) {
            subtask.setUpdatedAt(Instant.now());
        }
        todoSubtaskMapper.insertSelective(subtask);
        return subtask.getId();
    }

    private boolean updateTodoSubtask(UpdateSubtaskRequest request, long todoId) {
        var subtask = TodoConverter.INSTANCE.toTodoSubtaskEntity(request);
        if (subtask.getUpdatedAt() == null) {
            subtask.setUpdatedAt(Instant.now());
        }
        return todoSubtaskMapper.update(c -> TodoSubtaskMapper.updateSelectiveColumns(subtask, c)
                        .where(todoSubtask.id, isEqualTo(request.getId()))
                        .and(todoSubtask.todoId, isEqualTo(todoId))
                        .and(todoSubtask.deletedAt, isNull()))
                > 0;
    }

    private boolean deleteTodoSubtask(DeleteSubtaskRequest request, long todoId) {
        return todoSubtaskMapper.update(c -> c.set(todoSubtask.deletedAt)
                        .equalTo(Instant.now())
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
