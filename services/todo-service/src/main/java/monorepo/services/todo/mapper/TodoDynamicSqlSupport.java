package monorepo.services.todo.mapper;

import java.sql.JDBCType;
import java.time.Instant;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

/**
 * MyBatis Dynamic SQL support for Todo table
 *
 * @author Claude
 * @since 2025/11/22
 */
public final class TodoDynamicSqlSupport {
    public static final Todo todo = new Todo();
    public static final SqlColumn<String> id = todo.id;
    public static final SqlColumn<String> userId = todo.userId;
    public static final SqlColumn<String> title = todo.title;
    public static final SqlColumn<String> description = todo.description;
    public static final SqlColumn<String> status = todo.status;
    public static final SqlColumn<Integer> priority = todo.priority;
    public static final SqlColumn<String> assignee = todo.assignee;
    public static final SqlColumn<Instant> dueDate = todo.dueDate;
    public static final SqlColumn<Instant> createdAt = todo.createdAt;
    public static final SqlColumn<Instant> updatedAt = todo.updatedAt;

    public static final class Todo extends SqlTable {
        public final SqlColumn<String> id = column("id", JDBCType.VARCHAR);
        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR);
        public final SqlColumn<String> title = column("title", JDBCType.VARCHAR);
        public final SqlColumn<String> description = column("description", JDBCType.VARCHAR);
        public final SqlColumn<String> status = column("status", JDBCType.VARCHAR);
        public final SqlColumn<Integer> priority = column("priority", JDBCType.INTEGER);
        public final SqlColumn<String> assignee = column("assignee", JDBCType.VARCHAR);
        public final SqlColumn<Instant> dueDate = column("due_date", JDBCType.TIMESTAMP);
        public final SqlColumn<Instant> createdAt = column("created_at", JDBCType.TIMESTAMP);
        public final SqlColumn<Instant> updatedAt = column("updated_at", JDBCType.TIMESTAMP);

        public Todo() {
            super("todo");
        }
    }
}
