package monorepo.services.todo.converter;

import monorepo.lib.msp.MapStructConfig;
import monorepo.proto.todo.v1.TodoModel;
import monorepo.services.todo.entity.Todo;
import org.mapstruct.Mapper;
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

    public abstract TodoModel entityToModel(Todo entity);

    public abstract Todo modelToEntity(TodoModel model);
}
