package monorepo.services.product.converter;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.ReportingPolicy.ERROR;

import monorepo.proto.order.v1.EverythingEdition2023;
import monorepo.proto.order.v1.EverythingProto2;
import monorepo.proto.order.v1.EverythingProto3;
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

    // proto3
    @Mapping(target = "float_", source = "float")
    @Mapping(target = "double_", source = "double")
    @Mapping(target = "enum_", source = "enum")
    Everything proto3ToJavaBean(EverythingProto3 proto);

    @Mapping(target = "float", source = "float_")
    @Mapping(target = "double", source = "double_")
    @Mapping(target = "enum", source = "enum_")
    EverythingProto3 javaBeanToProto3(Everything javaBean);

    EverythingProto3 proto3ToProto3(EverythingProto3 proto);

    // proto2
    @Mapping(target = "float_", source = "float")
    @Mapping(target = "double_", source = "double")
    @Mapping(target = "enum_", source = "enum")
    Everything proto2ToJavaBean(EverythingProto2 proto);

    @Mapping(target = "float", source = "float_")
    @Mapping(target = "double", source = "double_")
    @Mapping(target = "enum", source = "enum_")
    EverythingProto2 javaBeanToProto2(Everything javaBean);

    EverythingProto2 proto2ToProto2(EverythingProto2 proto);

    // edition 2023
    @Mapping(target = "float_", source = "float")
    @Mapping(target = "double_", source = "double")
    @Mapping(target = "enum_", source = "enum")
    Everything edition2023ToJavaBean(EverythingEdition2023 proto);

    @Mapping(target = "float", source = "float_")
    @Mapping(target = "double", source = "double_")
    @Mapping(target = "enum", source = "enum_")
    EverythingEdition2023 javaBeanToEdition2023(Everything javaBean);

    EverythingEdition2023 edition2023ToEdition2023(EverythingEdition2023 proto);
}
