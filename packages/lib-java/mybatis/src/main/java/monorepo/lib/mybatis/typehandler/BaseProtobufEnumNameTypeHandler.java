package monorepo.lib.mybatis.typehandler;

import com.google.protobuf.ProtocolMessageEnum;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.ibatis.type.JdbcType;
import org.jspecify.annotations.Nullable;

/**
 * Base TypeHandler for Protobuf enums that serializes to/from string (varchar) using the enum's name.
 *
 * <p><b>Usage Example:</b>
 * <pre>{@code
 * // For a Protobuf enum stored as string in database
 * public class MyEnumTypeHandler extends BaseProtobufEnumNameTypeHandler<MyEnum> {
 * }
 * }</pre>
 *
 * @param <T> The Protobuf enum type
 * @author Freeman
 * @since 2025/12/4
 */
public abstract class BaseProtobufEnumNameTypeHandler<T extends Enum<T> & ProtocolMessageEnum>
        extends AbstractProtobufEnumTypeHandler<T> {

    private final Map<String, T> enumMap;

    protected BaseProtobufEnumNameTypeHandler() {
        this.enumMap = buildNameToEnumMap();
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public @Nullable T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return fromString(rs.getString(columnName), rs.wasNull());
    }

    @Override
    public @Nullable T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return fromString(rs.getString(columnIndex), rs.wasNull());
    }

    @Override
    public @Nullable T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return fromString(cs.getString(columnIndex), cs.wasNull());
    }

    @Nullable
    private T fromString(@Nullable String name, boolean wasNull) {
        if (wasNull) {
            return null;
        }
        if (name == null || name.isBlank()) {
            return defaultEnum;
        }
        return enumMap.getOrDefault(name, unrecognizedEnum);
    }

    private Map<String, T> buildNameToEnumMap() {
        var result = new LinkedHashMap<String, T>();
        for (var e : getEnumConstants()) {
            if (!UNRECOGNIZED.equals(e.name())) {
                result.put(e.name(), e);
            }
        }
        return result;
    }
}
