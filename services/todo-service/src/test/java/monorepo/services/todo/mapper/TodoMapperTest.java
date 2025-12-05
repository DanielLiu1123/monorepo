package monorepo.services.todo.mapper;

import static monorepo.services.todo.mapper.TodoDynamicSqlSupport.todo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mybatis.dynamic.sql.SqlBuilder.and;
import static org.mybatis.dynamic.sql.SqlBuilder.group;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThan;
import static org.mybatis.dynamic.sql.SqlBuilder.isLessThan;
import static org.mybatis.dynamic.sql.SqlBuilder.or;
import static org.mybatis.dynamic.sql.SqlBuilder.select;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import monorepo.proto.todo.v1.Todo;
import org.junit.jupiter.api.Test;
import org.mybatis.dynamic.sql.AndOrCriteriaGroup;
import org.mybatis.dynamic.sql.render.RenderingStrategies;

class TodoMapperTest {

    @Test
    void testComplexAndOrConditions() {
        var conditions = new ArrayList<AndOrCriteriaGroup>();
        conditions.add(or(List.of(and(todo.createdAt, isLessThan(LocalDateTime.now())))));
        conditions.add(or(List.of(
                and(todo.updatedAt, isEqualTo(LocalDateTime.now())),
                and(todo.priority, isLessThan(Todo.Priority.HIGH)))));
        conditions.add(or(List.of(
                and(todo.updatedAt, isEqualTo(LocalDateTime.now())),
                and(todo.priority, isEqualTo(Todo.Priority.HIGH)),
                and(todo.id, isGreaterThan(1L)))));

        var actual = select(TodoMapper.selectList)
                .from(todo)
                .where()
                .and(todo.userId, isEqualTo(1L))
                .and(group(conditions))
                .build()
                .render(RenderingStrategies.MYBATIS3)
                .getSelectStatement();
        IO.println(actual);

        var expected = """
                select id, user_id, title, description, "state", priority, assignee, due_date, created_at, updated_at, deleted_at
                from todo
                where user_id = #{parameters.p1,jdbcType=BIGINT}
                  and (created_at < #{parameters.p2,jdbcType=TIMESTAMP}
                    or (updated_at = #{parameters.p3,jdbcType=TIMESTAMP} and priority < #{parameters.p4,jdbcType=VARCHAR,typeHandler=monorepo.services.todo.entity.typehandler.TodoPriorityTypeHandler})
                    or (updated_at = #{parameters.p5,jdbcType=TIMESTAMP} and priority = #{parameters.p6,jdbcType=VARCHAR,typeHandler=monorepo.services.todo.entity.typehandler.TodoPriorityTypeHandler} and id > #{parameters.p7,jdbcType=BIGINT}))
                """.trim();

        assertThat(actual).isEqualToIgnoringWhitespace(expected);
    }
}
