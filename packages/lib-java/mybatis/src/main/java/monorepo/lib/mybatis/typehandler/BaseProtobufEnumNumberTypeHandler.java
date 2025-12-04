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
import java.util.Optional;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.jspecify.annotations.Nullable;
import org.springframework.core.ResolvableType;

/**
 * Base TypeHandler for Protobuf enums that serializes to/from integer (int) using the enum's number value.
 *
 * <p><b>Usage Example:</b>
 * <pre>{@code
 * // For a Protobuf enum stored as integer in database
 * public class MyEnumTypeHandler extends BaseProtobufEnumNumberTypeHandler<MyEnum> {
 * }
 * }</pre>
 *
 * @param <T> The Protobuf enum type
 * @author Freeman
 * @since 2025/12/4
 */
public abstract class BaseProtobufEnumNumberTypeHandler<T extends Enum<T> & ProtocolMessageEnum>
        extends BaseTypeHandler<T> {
    private static final String UNRECOGNIZED = "UNRECOGNIZED";

    private final Map<Integer, T> enumMap;
    private final T defaultEnum;
    private final T unrecognizedEnum;

    @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
    protected BaseProtobufEnumNumberTypeHandler() {
        T[] enumConstants =
                Optional.ofNullable(resolveEnumType().getEnumConstants()).orElseThrow();
        this.enumMap = buildNumberToEnumMap(enumConstants);
        this.defaultEnum = findDefaultEnum(enumConstants);
        this.unrecognizedEnum = findUnrecognizedEnum(enumConstants);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getNumber());
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return fromInt(rs.getInt(columnName), rs.wasNull());
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return fromInt(rs.getInt(columnIndex), rs.wasNull());
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return fromInt(cs.getInt(columnIndex), cs.wasNull());
    }

    @Nullable
    private T fromInt(Integer number, boolean wasNull) {
        if (wasNull) {
            return null;
        }
        if (number == null || number == 0) {
            return defaultEnum;
        }
        return enumMap.getOrDefault(number, unrecognizedEnum);
    }

    @SuppressWarnings("unchecked")
    private Class<T> resolveEnumType() {
        Class<?> result = ResolvableType.forClass(getClass())
                .as(BaseProtobufEnumNumberTypeHandler.class)
                .resolveGeneric(0);
        Objects.requireNonNull(result, "Cannot resolve enum type");
        return (Class<T>) result;
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
