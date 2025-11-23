package monorepo.services.todo.mapper;

import jakarta.annotation.Generated;
import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class TodoSubtaskDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    public static final TodoSubtask todoSubtask = new TodoSubtask();

    /**
     * Database Column Remarks:
     *   Primary key of the subtask
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo_subtask.id")
    public static final SqlColumn<Long> id = todoSubtask.id;

    /**
     * Database Column Remarks:
     *   Foreign key referencing the associated todo item
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo_subtask.todo_id")
    public static final SqlColumn<Long> todoId = todoSubtask.todoId;

    /**
     * Database Column Remarks:
     *   Title of the subtask
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo_subtask.title")
    public static final SqlColumn<String> title = todoSubtask.title;

    /**
     * Database Column Remarks:
     *   Timestamp when the subtask was created
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo_subtask.created_at")
    public static final SqlColumn<LocalDateTime> createdAt = todoSubtask.createdAt;

    /**
     * Database Column Remarks:
     *   Timestamp when the subtask was last updated
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo_subtask.updated_at")
    public static final SqlColumn<LocalDateTime> updatedAt = todoSubtask.updatedAt;

    /**
     * Database Column Remarks:
     *   Timestamp when the subtask was deleted (soft delete)
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo_subtask.deleted_at")
    public static final SqlColumn<LocalDateTime> deletedAt = todoSubtask.deletedAt;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo_subtask")
    public static final class TodoSubtask extends AliasableSqlTable<TodoSubtask> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<Long> todoId = column("todo_id", JDBCType.BIGINT);

        public final SqlColumn<String> title = column("title", JDBCType.VARCHAR);

        public final SqlColumn<LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP);

        public final SqlColumn<LocalDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP);

        public final SqlColumn<LocalDateTime> deletedAt = column("deleted_at", JDBCType.TIMESTAMP);

        public TodoSubtask() {
            super("todo_subtask", TodoSubtask::new);
        }
    }
}