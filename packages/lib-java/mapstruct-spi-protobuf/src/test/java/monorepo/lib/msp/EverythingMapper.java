package monorepo.lib.msp;

import static org.mapstruct.ReportingPolicy.ERROR;

import everything.Everything;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class, unmappedSourcePolicy = ERROR)
public abstract class EverythingMapper {

    public static final EverythingMapper INSTANCE = Mappers.getMapper(EverythingMapper.class);

    @Mapping(target = "float_", source = "float")
    @Mapping(target = "double_", source = "double")
    @Mapping(target = "enum_", source = "enum")
    abstract EverythingDTO proto3ToJavaBean(Everything proto);

    @Mapping(target = "float", source = "float_")
    @Mapping(target = "double", source = "double_")
    @Mapping(target = "enum", source = "enum_")
    abstract Everything javaBeanToProto3(EverythingDTO javaBean);

    Everything.Message javaBeanMessageToProtobufMessage(EverythingDTO.Message value) {
        var builder = Everything.Message.newBuilder();
        builder.setId(value.id());
        builder.setName(value.name());
        return builder.build();
    }

    EverythingDTO.Message protobufMessageToJavaBeanMessage(Everything.Message value) {
        return new EverythingDTO.Message(value.getId(), value.getName());
    }
}
