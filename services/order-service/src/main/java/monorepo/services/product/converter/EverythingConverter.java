package monorepo.services.product.converter;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.ReportingPolicy.ERROR;

import monorepo.proto.order.v1.EverythingModel;
import monorepo.services.product.entity.Everything;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/8
 */
@Mapper(nullValueCheckStrategy = ALWAYS, unmappedTargetPolicy = ERROR, unmappedSourcePolicy = ERROR)
public interface EverythingConverter {

    EverythingConverter INSTANCE = Mappers.getMapper(EverythingConverter.class);

    @Mapping(target = "float_", source = "float")
    @Mapping(target = "double_", source = "double")
    @Mapping(target = "enum_", source = "enum")
    Everything modelToEntity(EverythingModel model);

    @Mapping(target = "float", source = "float_")
    @Mapping(target = "double", source = "double_")
    @Mapping(target = "enum", source = "enum_")
    EverythingModel entityToModel(Everything entity);

    EverythingModel modelToModel(EverythingModel model);
}
