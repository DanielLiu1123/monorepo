package monorepo.services.todo.service;

import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.id;
import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.userId;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import monorepo.services.todo.entity.TodoEntity;
import monorepo.services.todo.mapper.TodoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Todo business service with multi-tenant support
 *
 * @author Claude
 * @since 2025/11/22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoMapper todoMapper;

    /**
     * Create a new todo for the specified user
     *
     * @param entity the todo entity to create
     * @param currentUserId the user ID of the current user (tenant)
     * @return the created todo entity
     */
    @Transactional
    public TodoEntity createTodo(TodoEntity entity, String currentUserId) {
        entity.setId(UUID.randomUUID().toString());
        entity.setUserId(currentUserId); // Set tenant
        Instant now = Instant.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        if (entity.getStatus() == null || entity.getStatus().isEmpty()) {
            entity.setStatus("PENDING");
        }
        if (entity.getPriority() == null) {
            entity.setPriority(2); // Default to MEDIUM priority
        }

        todoMapper.insert(entity);
        log.debug("Created todo with id: {} for user: {}", entity.getId(), currentUserId);
        return entity;
    }

    /**
     * Get a todo by ID for the specified user
     *
     * @param todoId the todo ID
     * @param currentUserId the user ID of the current user (tenant)
     * @return the todo entity if found and belongs to the user
     */
    public Optional<TodoEntity> getTodo(String todoId, String currentUserId) {
        return todoMapper.selectOne(c -> c.where(id, isEqualTo(todoId)).and(userId, isEqualTo(currentUserId)));
    }

    /**
     * Batch get todos by IDs for the specified user
     *
     * @param ids the list of todo IDs
     * @param currentUserId the user ID of the current user (tenant)
     * @return list of todos that belong to the user
     */
    public List<TodoEntity> batchGetTodos(List<String> ids, String currentUserId) {
        return todoMapper.select(c -> c.where(id, isIn(ids)).and(userId, isEqualTo(currentUserId)));
    }

    /**
     * List todos for the specified user with pagination
     *
     * @param currentUserId the user ID of the current user (tenant)
     * @param pageSize the maximum number of items to return
     * @param pageToken the page token for pagination
     * @return paginated result with todos
     */
    public ListTodosResult listTodos(String currentUserId, int pageSize, String pageToken) {
        // For simplicity, we'll use offset-based pagination
        int offset = 0;
        if (pageToken != null && !pageToken.isEmpty()) {
            try {
                offset = Integer.parseInt(pageToken);
            } catch (NumberFormatException e) {
                log.warn("Invalid page token: {}", pageToken);
            }
        }

        // Select todos for this user only (tenant isolation)
        List<TodoEntity> todos = todoMapper.select(c -> c.where(userId, isEqualTo(currentUserId)));

        // Calculate pagination
        int start = Math.min(offset, todos.size());
        int end = Math.min(offset + pageSize, todos.size());
        List<TodoEntity> page = todos.subList(start, end);

        boolean hasMore = end < todos.size();
        String nextPageToken = hasMore ? String.valueOf(offset + pageSize) : "";

        return new ListTodosResult(page, nextPageToken, todos.size());
    }

    /**
     * Update a todo for the specified user
     *
     * @param entity the todo entity with updated values
     * @param currentUserId the user ID of the current user (tenant)
     * @return the updated todo entity if found and belongs to the user
     */
    @Transactional
    public Optional<TodoEntity> updateTodo(TodoEntity entity, String currentUserId) {
        // Verify the todo belongs to the current user before updating
        Optional<TodoEntity> existing = getTodo(entity.getId(), currentUserId);
        if (existing.isEmpty()) {
            log.warn("Todo not found or access denied: {} for user: {}", entity.getId(), currentUserId);
            return Optional.empty();
        }

        entity.setUserId(currentUserId); // Ensure userId cannot be changed
        entity.setUpdatedAt(Instant.now());
        todoMapper.updateByPrimaryKeySelective(entity);

        return todoMapper.selectByPrimaryKey(entity.getId());
    }

    /**
     * Delete a todo for the specified user
     *
     * @param todoId the todo ID to delete
     * @param currentUserId the user ID of the current user (tenant)
     * @return true if deleted, false if not found or access denied
     */
    @Transactional
    public boolean deleteTodo(String todoId, String currentUserId) {
        // Verify the todo belongs to the current user before deleting
        Optional<TodoEntity> existing = getTodo(todoId, currentUserId);
        if (existing.isEmpty()) {
            log.warn("Todo not found or access denied: {} for user: {}", todoId, currentUserId);
            return false;
        }

        int deleted = todoMapper.deleteByPrimaryKey(todoId);
        log.debug("Deleted todo with id: {} for user: {}, result: {}", todoId, currentUserId, deleted);
        return deleted > 0;
    }

    public record ListTodosResult(List<TodoEntity> todos, String nextPageToken, int totalSize) {}
}
