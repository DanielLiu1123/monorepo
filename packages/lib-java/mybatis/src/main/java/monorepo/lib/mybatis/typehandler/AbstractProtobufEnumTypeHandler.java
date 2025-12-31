package monorepo.lib.mybatis.typehandler;

import com.google.protobuf.ProtocolMessageEnum;
import java.util.Objects;
import org.apache.ibatis.type.BaseTypeHandler;
import org.springframework.core.ResolvableType;

/**
 * Abstract base class for Protobuf enum TypeHandlers.
 *
 * @param <T> The Protobuf enum type
 * @author Freeman
 * @since 2025/12/28
 */
abstract class AbstractProtobufEnumTypeHandler<T extends Enum<T> & ProtocolMessageEnum> extends BaseTypeHandler<T> {
    protected static final String UNRECOGNIZED = "UNRECOGNIZED";

    protected final T defaultEnum;
    protected final T unrecognizedEnum;

    protected AbstractProtobufEnumTypeHandler() {
        this.defaultEnum = getDefaultEnum();
        this.unrecognizedEnum = getUnrecognizedEnum();
    }

    protected T[] getEnumConstants() {
        return Objects.requireNonNull(resolveEnumType(getClass()).getEnumConstants());
    }

    @SuppressWarnings("unchecked")
    private Class<T> resolveEnumType(Class<?> clazz) {
        Class<?> result = ResolvableType.forClass(clazz)
                .as(AbstractProtobufEnumTypeHandler.class)
                .resolveGeneric(0);
        Objects.requireNonNull(result, "Cannot resolve enum type");
        return (Class<T>) result;
    }

    private T getDefaultEnum() {
        for (var e : getEnumConstants()) {
            if (e.getNumber() == 0) {
                return e;
            }
        }
        throw new IllegalArgumentException("Protobuf enum should have a default value with number 0");
    }

    private T getUnrecognizedEnum() {
        for (var e : getEnumConstants()) {
            if (UNRECOGNIZED.equals(e.name())) {
                return e;
            }
        }
        throw new IllegalArgumentException("Protobuf enum should have an UNRECOGNIZED value");
    }
}
