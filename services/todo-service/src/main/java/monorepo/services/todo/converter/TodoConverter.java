package monorepo.services.todo.converter;

import monorepo.lib.msp.MapStructConfig;
import monorepo.proto.todo.v1.CreateTodoRequest;
import monorepo.proto.todo.v1.TodoModel;
import monorepo.proto.todo.v1.UpdateTodoRequest;
import monorepo.services.todo.entity.Todo;
import monorepo.services.todo.entity.TodoSubtask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/22
 */
@Mapper(config = MapStructConfig.class)
public abstract class TodoConverter {

    public static final TodoConverter INSTANCE = Mappers.getMapper(TodoConverter.class);

    public abstract TodoModel.Todo toTodoModel(Todo entity);

    public abstract TodoModel.SubTask toTodoSubtaskModel(TodoSubtask entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract Todo toTodo(CreateTodoRequest.Todo request);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract Todo toTodo(UpdateTodoRequest.Todo request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "todoId", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract TodoSubtask toTodoSubtask(CreateTodoRequest.SubTask request);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "todoId", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract TodoSubtask toTodoSubtask(UpdateTodoRequest.SubTask.Update request);
}
