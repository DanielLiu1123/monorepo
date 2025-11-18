package monorepo.lib.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import jacksonmodule.protobuf.v3.ProtobufModule;
import java.util.List;
import monorepo.lib.common.json.BigNumberModule;
import org.springframework.core.ParameterizedTypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 *
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

    public static <T> T toObject(String json, Class<T> clazz) {
        return jsonMapper.readValue(json, clazz);
    }

    public static <T> T toObject(String json, ParameterizedTypeReference<T> typeRef) {
        return jsonMapper.readValue(json, jsonMapper.getTypeFactory().constructType(typeRef.getType()));
    }

    public static <T> List<T> toList(String json, Class<T> elementClazz) {
        return jsonMapper.readValue(
                json, jsonMapper.getTypeFactory().constructCollectionType(List.class, elementClazz));
    }

    public static String toJson(Object obj) {
        return jsonMapper.writeValueAsString(obj);
    }
}
