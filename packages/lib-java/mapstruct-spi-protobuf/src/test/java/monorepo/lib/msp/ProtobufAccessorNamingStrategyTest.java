package monorepo.lib.msp;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;
import org.mapstruct.ap.MappingProcessor;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;

/**
 * Tests for ProtobufAccessorNamingStrategy using Google's compile-testing library
 * with proto-order package protobuf messages.
 */
class ProtobufAccessorNamingStrategyTest {

    /**
     * Test that MapStruct correctly maps simple scalar fields from protobuf
     */
    @Test
    void testSimpleScalarFieldMapping() {
        JavaFileObject dto = JavaFileObjects.forSourceString("test.OrderDTO", """
                package test;
                
                import java.util.List;
                import java.util.Map;
                
                public class OrderDTO {
                    private long id;
                    private List<Long> itemIds;
                    private Map<String, String> attributes;
                
                    public long getId() {
                        return id;
                    }
                    public void setId(long id) {
                        this.id = id;
                    }
                    public List<Long> getItemIds() {
                        return itemIds;
                    }
                    public void setItemIds(List<Long> itemIds) {
                        this.itemIds = itemIds;
                    }
                    public Map<String, String> getAttributes() {
                        return attributes;
                    }
                    public void setAttributes(Map<String, String> attributes) {
                        this.attributes = attributes;
                    }
                }
                """);

        JavaFileObject mapper = JavaFileObjects.forSourceString("test.OrderMapper", """
                package test;
                
                import org.mapstruct.Mapper;
                import org.mapstruct.factory.Mappers;
                import monorepo.proto.order.v1.OrderModel;
                
                @Mapper
                public interface OrderMapper {
                    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
                
                    OrderDTO modelToDTO(OrderModel orderModel);
                    OrderModel dtoToModel(OrderDTO orderDTO);
                }
                """);

        Compilation compilation = Compiler.javac()
                .withProcessors(new MappingProcessor())
                .compile(dto, mapper);

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("test.OrderMapperImpl")
                .contentsAsUtf8String()
                .contains("getId()");
    }

}