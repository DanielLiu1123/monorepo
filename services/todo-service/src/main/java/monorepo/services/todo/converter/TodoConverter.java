package monorepo.services.todo.converter;

import monorepo.lib.msp.MapStructConfig;
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
}
