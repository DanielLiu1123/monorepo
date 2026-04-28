package monorepo.services.todo.mapper;

import java.time.LocalDate;
import java.util.List;
import monorepo.lib.common.util.JsonUtil;
import monorepo.services.todo.entity.Todo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectWriter;

@SpringBootTest
class TodoMapperIT {

    @Autowired
    TodoMapper todoMapper;

    @Test
    @Transactional
    void testTypeHandler() {
        Todo todo = new Todo();
        todo.setUserId(1L);
        todo.setTitle("Test Todo");
        todo.setDescription("Test Description");
        todo.setState(monorepo.proto.todo.v1.Todo.State.PENDING);
        todo.setPriority(monorepo.proto.todo.v1.Todo.Priority.LOW);
        todo.setAssignee(2L);
        todo.setDueDate(LocalDate.now().plusDays(7));
        todo.setAttributes(List.of(
                monorepo.proto.todo.v1.Todo.Attribute.newBuilder()
                        .setKey("key1")
                        .setValue("value1")
                        .build(),
                monorepo.proto.todo.v1.Todo.Attribute.newBuilder()
                        .setKey("key2")
                        .setValue("value2")
                        .build()));
        todo.setTags(List.of("tag1", "tag2"));
        todoMapper.insertSelective(todo);

        var result = todoMapper.selectByPrimaryKey(todo.getId());
        IO.println(JsonUtil.stringify(result, ObjectWriter::withDefaultPrettyPrinter));
    }
}
