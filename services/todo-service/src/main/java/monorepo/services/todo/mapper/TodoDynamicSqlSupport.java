package monorepo.services.todo.mapper;

import jakarta.annotation.Generated;
import java.sql.JDBCType;
import java.time.Instant;
import java.time.LocalDate;
import monorepo.proto.todo.v1.Todo.Priority;
import monorepo.proto.todo.v1.Todo.State;
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
    public static final SqlColumn<Instant> createdAt = todo.createdAt;

    /**
     * Database Column Remarks:
     *   Last update timestamp
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo.updated_at")
    public static final SqlColumn<Instant> updatedAt = todo.updatedAt;

    /**
     * Database Column Remarks:
     *   Deletion timestamp for soft deletes
     */
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: todo.deleted_at")
    public static final SqlColumn<Instant> deletedAt = todo.deletedAt;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: todo")
    public static final class Todo extends AliasableSqlTable<Todo> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<Long> userId = column("user_id", JDBCType.BIGINT).withJavaProperty("userId");

        public final SqlColumn<String> title = column("title", JDBCType.VARCHAR).withJavaProperty("title");

        public final SqlColumn<String> description = column("description", JDBCType.VARCHAR).withJavaProperty("description");

        public final SqlColumn<State> state = column("\"state\"", JDBCType.SMALLINT, "monorepo.lib.mybatis.typehandler.ProtobufEnumTypeHandler").withJavaType(State.class).withJavaProperty("state");

        public final SqlColumn<Priority> priority = column("priority", JDBCType.VARCHAR, "monorepo.lib.mybatis.typehandler.ProtobufEnumTypeHandler").withJavaType(Priority.class).withJavaProperty("priority");

        public final SqlColumn<Long> assignee = column("assignee", JDBCType.BIGINT).withJavaProperty("assignee");

        public final SqlColumn<LocalDate> dueDate = column("due_date", JDBCType.DATE).withJavaProperty("dueDate");

        public final SqlColumn<Instant> createdAt = column("created_at", JDBCType.TIMESTAMP).withJavaProperty("createdAt");

        public final SqlColumn<Instant> updatedAt = column("updated_at", JDBCType.TIMESTAMP).withJavaProperty("updatedAt");

        public final SqlColumn<Instant> deletedAt = column("deleted_at", JDBCType.TIMESTAMP).withJavaProperty("deletedAt");

        public Todo() {
            super("todo", Todo::new);
        }
    }
}