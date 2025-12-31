package monorepo.services.todo.converter;

import java.util.Collection;
import monorepo.lib.msp.MapStructConfig;
import monorepo.proto.todo.v1.CreateSubtaskRequest;
import monorepo.proto.todo.v1.CreateTodoRequest;
import monorepo.proto.todo.v1.UpdateSubtaskRequest;
import monorepo.proto.todo.v1.UpdateTodoRequest;
import monorepo.services.todo.entity.Todo;
import monorepo.services.todo.entity.TodoSubtask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Todo converter.
 *
 * @author Freeman
 * @since 2025/11/22
 */
@Mapper(config = MapStructConfig.class)
public abstract class TodoConverter {

    public static final TodoConverter INSTANCE = Mappers.getMapper(TodoConverter.class);

    public monorepo.proto.todo.v1.Todo buildTodo(Todo todo, Collection<TodoSubtask> subTasks) {
        var builder = toTodo(todo).toBuilder();
        for (var subTask : subTasks) {
            builder.addSubTasks(toTodoSubtask(subTask));
        }
        return builder.build();
    }

    @Mapping(target = "subTasks", ignore = true)
    public abstract monorepo.proto.todo.v1.Todo toTodo(Todo entity);

    public abstract monorepo.proto.todo.v1.SubTask toTodoSubtask(TodoSubtask entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract Todo toTodoEntity(CreateTodoRequest request);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract Todo toTodoEntity(UpdateTodoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "todoId", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract TodoSubtask toTodoSubtaskEntity(CreateSubtaskRequest request);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "todoId", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract TodoSubtask toTodoSubtaskEntity(UpdateSubtaskRequest request);
}
