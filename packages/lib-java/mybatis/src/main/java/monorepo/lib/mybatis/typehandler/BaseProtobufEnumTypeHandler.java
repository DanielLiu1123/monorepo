package monorepo.lib.mybatis.typehandler;

import com.google.protobuf.ProtocolMessageEnum;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.jspecify.annotations.Nullable;
import org.springframework.core.ResolvableType;

/**
 * Base TypeHandler for Protobuf enum, supports mapping from/to String(varchar) or Integer(int).
 *
 * <p> Example:
 * <pre>{@code
 * // If db column is varchar type
 * public class MyEnumTypeHandler extends BaseProtobufEnumTypeHandler<MyEnum, String> {
 * }
 *
 * // If db column is int type
 * public class MyEnumTypeHandler extends BaseProtobufEnumTypeHandler<MyEnum, Integer> {
 * }
 * }</pre>
 *
 *
 * @param <T> Target type, convert source type to this type.
 * @param <S> Source type, convert this type to target type.
 * @author Freeman
 * @since 2025/11/22
 */
public abstract class BaseProtobufEnumTypeHandler<T extends Enum<T> & ProtocolMessageEnum, S>
        extends BaseTypeHandler<T> {

    private static final String UNRECOGNIZED = "UNRECOGNIZED";

    private final Map<S, T> enumMap;
    private final T defaultEnum;
    private final T unrecognizedEnum;
    private final Class<S> sourceType;

    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    @SuppressWarnings("unchecked")
    protected BaseProtobufEnumTypeHandler() {
        var enumType = resolveEnumType();
        var sourceType = (Class<S>) resolveSourceType();

        validateSourceType(sourceType);

        T[] enumConstants = enumType.getEnumConstants();
        this.enumMap = sourceType == String.class
                ? (Map<S, T>) buildNameToEnumMap(enumConstants)
                : (Map<S, T>) buildNumberToEnumMap(enumConstants);
        this.defaultEnum = findDefaultEnum(enumConstants);
        this.unrecognizedEnum = findUnrecognizedEnum(enumConstants);
        this.sourceType = sourceType;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        if (sourceType == String.class) {
            ps.setString(i, parameter.name());
        } else {
            ps.setInt(i, parameter.getNumber());
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return sourceType == String.class
                ? fromString(rs.getString(columnName), rs.wasNull())
                : fromInt(rs.getInt(columnName), rs.wasNull());
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return sourceType == String.class
                ? fromString(rs.getString(columnIndex), rs.wasNull())
                : fromInt(rs.getInt(columnIndex), rs.wasNull());
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return sourceType == String.class
                ? fromString(cs.getString(columnIndex), cs.wasNull())
                : fromInt(cs.getInt(columnIndex), cs.wasNull());
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private T fromString(@Nullable String name, boolean wasNull) {
        if (wasNull) {
            return null;
        }
        if (name == null || name.isBlank()) {
            return defaultEnum;
        }
        return enumMap.getOrDefault((S) name, unrecognizedEnum);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private T fromInt(Integer number, boolean wasNull) {
        if (wasNull) {
            return null;
        }
        if (number == null || number == 0) {
            return defaultEnum;
        }
        return enumMap.getOrDefault((S) number, unrecognizedEnum);
    }

    @SuppressWarnings("unchecked")
    private Class<T> resolveEnumType() {
        Class<?> result = ResolvableType.forClass(getClass())
                .as(BaseProtobufEnumTypeHandler.class)
                .resolveGeneric(0);
        Objects.requireNonNull(result, "Cannot resolve enum type");
        return (Class<T>) result;
    }

    private Class<?> resolveSourceType() {
        Class<?> result = ResolvableType.forClass(getClass())
                .as(BaseProtobufEnumTypeHandler.class)
                .resolveGeneric(1);
        Objects.requireNonNull(result, "Cannot resolve source type");
        return result;
    }

    private static void validateSourceType(Class<?> type) {
        if (type != String.class && type != Integer.class) {
            throw new IllegalArgumentException("Source type must be String or Integer");
        }
    }

    private Map<String, T> buildNameToEnumMap(T[] enumConstants) {
        var result = new LinkedHashMap<String, T>();
        for (var e : enumConstants) {
            if (!UNRECOGNIZED.equals(e.name())) {
                result.put(e.name(), e);
            }
        }
        return result;
    }

    private Map<Integer, T> buildNumberToEnumMap(T[] enumConstants) {
        var result = new LinkedHashMap<Integer, T>();
        for (var e : enumConstants) {
            if (!UNRECOGNIZED.equals(e.name())) {
                result.put(e.getNumber(), e);
            }
        }
        return result;
    }

    private T findDefaultEnum(T[] enumConstants) {
        for (var e : enumConstants) {
            if (e.getNumber() == 0) {
                return e;
            }
        }
        throw new IllegalArgumentException("Protobuf enum should have a default value with number 0");
    }

    private T findUnrecognizedEnum(T[] enumConstants) {
        for (var e : enumConstants) {
            if (UNRECOGNIZED.equals(e.name())) {
                return e;
            }
        }
        throw new IllegalArgumentException("Protobuf enum should have an UNRECOGNIZED value");
    }
}
