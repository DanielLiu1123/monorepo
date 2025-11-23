package monorepo.services.todo.mapper;

import jakarta.annotation.Generated;
import java.sql.JDBCType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import monorepo.proto.todo.v1.TodoModel.Priority;
import monorepo.proto.todo.v1.TodoModel.State;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class TodoDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    public static final Todo todo = new Todo();

    /**
     * Database Column Remarks:
     *   Unique identifier
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo.id")
    public static final SqlColumn<Long> id = todo.id;

    /**
     * Database Column Remarks:
     *   User ID - owner/tenant of this todo
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo.user_id")
    public static final SqlColumn<Long> userId = todo.userId;

    /**
     * Database Column Remarks:
     *   Todo title
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo.title")
    public static final SqlColumn<String> title = todo.title;

    /**
     * Database Column Remarks:
     *   Todo detailed description
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo.description")
    public static final SqlColumn<String> description = todo.description;

    /**
     * Database Column Remarks:
     *   Todo state
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo.state")
    public static final SqlColumn<State> state = todo.state;

    /**
     * Database Column Remarks:
     *   Priority level
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo.priority")
    public static final SqlColumn<Priority> priority = todo.priority;

    /**
     * Database Column Remarks:
     *   Assignee user ID
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo.assignee")
    public static final SqlColumn<Long> assignee = todo.assignee;

    /**
     * Database Column Remarks:
     *   Due date
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo.due_date")
    public static final SqlColumn<LocalDate> dueDate = todo.dueDate;

    /**
     * Database Column Remarks:
     *   Creation timestamp
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo.created_at")
    public static final SqlColumn<LocalDateTime> createdAt = todo.createdAt;

    /**
     * Database Column Remarks:
     *   Last update timestamp
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo.updated_at")
    public static final SqlColumn<LocalDateTime> updatedAt = todo.updatedAt;

    /**
     * Database Column Remarks:
     *   Deletion timestamp for soft deletes
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo.deleted_at")
    public static final SqlColumn<LocalDateTime> deletedAt = todo.deletedAt;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    public static final class Todo extends AliasableSqlTable<Todo> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<Long> userId = column("user_id", JDBCType.BIGINT);

        public final SqlColumn<String> title = column("title", JDBCType.VARCHAR);

        public final SqlColumn<String> description = column("description", JDBCType.VARCHAR);

        public final SqlColumn<State> state = column("\"state\"", JDBCType.SMALLINT, "monorepo.services.todo.entity.typehandler.TodoStateTypeHandler");

        public final SqlColumn<Priority> priority = column("priority", JDBCType.VARCHAR, "monorepo.services.todo.entity.typehandler.TodoPriorityTypeHandler");

        public final SqlColumn<Long> assignee = column("assignee", JDBCType.BIGINT);

        public final SqlColumn<LocalDate> dueDate = column("due_date", JDBCType.DATE);

        public final SqlColumn<LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP);

        public final SqlColumn<LocalDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP);

        public final SqlColumn<LocalDateTime> deletedAt = column("deleted_at", JDBCType.TIMESTAMP);

        public Todo() {
            super("todo", Todo::new);
        }
    }
}