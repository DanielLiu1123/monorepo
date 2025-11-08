package monorepo.services.product.converter;

import static com.google.testing.compile.CompilationSubject.assertThat;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;
import org.mapstruct.ap.MappingProcessor;

class EverythingConverterTest {

    @Test
    void compile() {
        // add java file objects
        var mapper = JavaFileObjects.forSourceString(
                "test.EverythingConverter",
                """
                package test;

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
                @Mapper(nullValueCheckStrategy = ALWAYS, unmappedTargetPolicy = ERROR)
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
                }
                """);

        Compilation compilation =
                Compiler.javac().withProcessors(new MappingProcessor()).compile(mapper);

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("test.EverythingConverterImpl")
                .contentsAsUtf8String()
                .contains("getId()");
    }
}
