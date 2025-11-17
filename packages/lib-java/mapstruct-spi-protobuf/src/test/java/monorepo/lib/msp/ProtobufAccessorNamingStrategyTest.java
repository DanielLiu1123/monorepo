package monorepo.lib.msp;

import static com.google.testing.compile.CompilationSubject.assertThat;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;
import org.mapstruct.ap.MappingProcessor;

/**
 * Tests for ProtobufAccessorNamingStrategy using Google's compile-testing library
 * with proto-order package protobuf messages.
 */
class ProtobufAccessorNamingStrategyTest {

    /**
     * Test that MapStruct correctly maps simple scalar fields from protobuf
     */
    @Test
    void testSimpleScalarFieldMapping() throws Exception {

        JavaFileObject dto = JavaFileObjects.forSourceString(
                EverythingDTO.class.getCanonicalName(),
                Files.readString(
                        Path.of(
                                "/Users/macbook/development/projects/idea/monorepo/packages/lib-java/mapstruct-spi-protobuf/src/test/java/monorepo/lib/msp/EverythingDTO.java")));

        JavaFileObject mapper = JavaFileObjects.forSourceString(
                EverythingMapper.class.getCanonicalName(),
                Files.readString(
                        Path.of(
                                "/Users/macbook/development/projects/idea/monorepo/packages/lib-java/mapstruct-spi-protobuf/src/test/java/monorepo/lib/msp/EverythingMapper.java")));

        Compilation compilation =
                Compiler.javac().withProcessors(new MappingProcessor()).compile(dto, mapper);

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("monorepo.lib.msp.EverythingMapperImpl")
                .contentsAsUtf8String()
                .contains("getString()");
    }
}
