package monorepo.lib.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import jacksonmodule.protobuf.v3.ProtobufModule;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import monorepo.lib.common.json.BigNumberModule;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.util.function.SingletonSupplier;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.json.JsonMapper;

/**
 * JSON utility.
 *
 * @author Freeman
 * @since 2025/11/18
 */
public final class JsonUtil {
    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    private JsonUtil() {}

    private static final Supplier<JsonMapper> jsonMapper = SingletonSupplier.of(JsonUtil::getJsonMapper);

    @SafeVarargs
    public static <T> T parse(String json, Class<T> clazz, Function<ObjectReader, ObjectReader>... customizers) {
        return parse(json, jsonMapper().constructType(clazz), customizers);
    }

    @SafeVarargs
    public static <T> T parse(
            String json, ParameterizedTypeReference<T> typeRef, Function<ObjectReader, ObjectReader>... customizers) {
        return parse(json, jsonMapper().constructType(typeRef.getType()), customizers);
    }

    @SafeVarargs
    public static <T> List<T> parseList(
            String json, Class<T> elementClazz, Function<ObjectReader, ObjectReader>... customizers) {
        return parse(
                json, jsonMapper().getTypeFactory().constructCollectionType(List.class, elementClazz), customizers);
    }

    @SafeVarargs
    public static String stringify(@Nullable Object obj, Function<ObjectWriter, ObjectWriter>... customizers) {
        if (customizers.length == 0) {
            return jsonMapper().writeValueAsString(obj);
        }
        var writer = jsonMapper().writer();
        for (var customizer : customizers) {
            writer = customizer.apply(writer);
        }
        return writer.writeValueAsString(obj);
    }

    @SafeVarargs
    @SuppressWarnings({"TypeParameterUnusedInFormals"})
    private static <T> T parse(String json, JavaType type, Function<ObjectReader, ObjectReader>... customizers) {
        if (customizers.length == 0) {
            return jsonMapper().readValue(json, type);
        }
        var reader = jsonMapper().readerFor(type);
        for (var customizer : customizers) {
            reader = customizer.apply(reader);
        }
        return reader.readValue(json);
    }

    private static JsonMapper jsonMapper() {
        return jsonMapper.get();
    }

    private static JsonMapper getJsonMapper() {
        // Use the JsonMapper bean from Spring context if available
        try {
            var bean = SpringUtil.getContext().getBean(JsonMapper.class);
            log.debug("Using JsonMapper bean from Spring context");
            return bean;
        } catch (Exception _) {
            log.debug("Not in Spring context or no JsonMapper bean found, using default JsonMapper");
            return defaultJsonMapper();
        }
    }

    private static JsonMapper defaultJsonMapper() {
        return JsonMapper.builder()
                .addModule(new ProtobufModule())
                .addModule(new BigNumberModule())
                .changeDefaultPropertyInclusion(value -> value.withValueInclusion(JsonInclude.Include.NON_NULL))
                .build();
    }
}
