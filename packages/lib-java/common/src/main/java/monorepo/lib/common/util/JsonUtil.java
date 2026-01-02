package monorepo.lib.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import jacksonmodule.protobuf.v3.ProtobufModule;
import java.util.List;
import java.util.function.Function;
import monorepo.lib.common.json.BigNumberModule;
import org.springframework.core.ParameterizedTypeReference;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.json.JsonMapper;

/**
 * JSON utility.
 *
 * @author Freeman
 * @since 2025/11/18
 */
public final class JsonUtil {

    private JsonUtil() {}

    private static final JsonMapper jsonMapper = JsonMapper.builder()
            .addModule(new ProtobufModule())
            .addModule(new BigNumberModule())
            .changeDefaultPropertyInclusion(value -> value.withValueInclusion(JsonInclude.Include.NON_NULL))
            .build();

    public static <T> T parse(String json, Class<T> clazz) {
        return jsonMapper.readValue(json, clazz);
    }

    public static <T> T parse(String json, ParameterizedTypeReference<T> typeRef) {
        return jsonMapper.readValue(json, jsonMapper.getTypeFactory().constructType(typeRef.getType()));
    }

    public static <T> List<T> parseList(String json, Class<T> elementClazz) {
        return jsonMapper.readValue(
                json, jsonMapper.getTypeFactory().constructCollectionType(List.class, elementClazz));
    }

    @SafeVarargs
    public static String stringify(Object obj, Function<ObjectWriter, ObjectWriter>... customizers) {
        if (customizers.length == 0) {
            return jsonMapper.writeValueAsString(obj);
        }
        var writer = jsonMapper.writer();
        for (var customizer : customizers) {
            writer = customizer.apply(writer);
        }
        return writer.writeValueAsString(obj);
    }
}
