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
        extends AbstractProtobufEnumTypeHandler<T> {

    private final Map<Integer, T> enumMap;

    protected BaseProtobufEnumNumberTypeHandler() {
        this.enumMap = buildNumberToEnumMap();
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

    private Map<Integer, T> buildNumberToEnumMap() {
        var result = new LinkedHashMap<Integer, T>();
        for (var e : getEnumConstants()) {
            if (!UNRECOGNIZED.equals(e.name())) {
                result.put(e.getNumber(), e);
            }
        }
        return result;
    }
}
