package monorepo.lib.common.util;

import jacksonmodule.protobuf.v3.ProtobufModule;
import tools.jackson.databind.json.JsonMapper;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/18
 */
public final class JsonUtil {

    private static final JsonMapper json =
            JsonMapper.builder().addModule(new ProtobufModule()).build();

    private JsonUtil() {}

    public static <T> T fromJson(String jsonStr, Class<T> clazz) {
        return json.readValue(jsonStr, clazz);
    }

    public static String toJson(Object obj) {
        return json.writeValueAsString(obj);
    }
}
