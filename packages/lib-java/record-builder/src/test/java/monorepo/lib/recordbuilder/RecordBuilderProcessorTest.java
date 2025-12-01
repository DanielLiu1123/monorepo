package monorepo.lib.recordbuilder;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author Freeman
 * @since 2025/12/1
 */
class RecordBuilderProcessorTest {

    @Test
    void testRecordBuilderProcessor() throws Exception {

        JavaFileObject source = loadTestSource(Everything.class);

        Compilation compilation =
                Compiler.javac().withProcessors(new RecordBuilderProcessor()).compile(source);

        CompilationSubject.assertThat(compilation).succeeded();
        CompilationSubject.assertThat(compilation)
                .generatedSourceFile("monorepo.lib.recordbuilder.EverythingBuilder")
                .contentsAsUtf8String()
                .contains("getString()");
    }

    private static JavaFileObject loadTestSource(Class<?> clazz) throws IOException {
        Path projectDir = Path.of("").toAbsolutePath();
        Path testSourceRoot = projectDir.resolve("src/test/java");
        String relativePath = clazz.getName().replace('.', '/') + ".java";
        return JavaFileObjects.forSourceString(
                clazz.getCanonicalName(), Files.readString(testSourceRoot.resolve(relativePath)));
    }
}
