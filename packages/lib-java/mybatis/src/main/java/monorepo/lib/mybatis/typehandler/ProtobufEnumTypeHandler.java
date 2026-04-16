package monorepo.lib.mybatis.typehandler;

import com.google.protobuf.ProtocolMessageEnum;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.jspecify.annotations.Nullable;

/**
 * Convert Protobuf enum to/from string or numeric value.
 *
 * <p>Writing to DB:
 * <ul>
 *   <li>Numeric JDBC types (TINYINT, SMALLINT, INTEGER, BIGINT, FLOAT, DOUBLE, NUMERIC, DECIMAL)
 *       → stores the protobuf number (e.g. {@code 1})
 *   <li>All other types → stores the enum name (e.g. {@code "STATUS_ACTIVE"})
 * </ul>
 *
 * <p>Reading from DB:
 * <ul>
 *   <li>{@link Number} value → looks up by protobuf number
 *   <li>Numeric string (e.g. {@code "1"}) → looks up by protobuf number
 *   <li>Name string (e.g. {@code "STATUS_ACTIVE"}) → looks up by enum name
 *   <li>{@code null} / SQL NULL → returns {@code null}
 *   <li>Empty / blank string → returns the default enum (number == 0)
 *   <li>Unrecognized value → returns the {@code UNRECOGNIZED} enum constant
 * </ul>
 *
 * @param <T> The Protobuf enum type
 * @author Freeman
 * @since 2026/4/16
 */
public class ProtobufEnumTypeHandler<T extends Enum<T> & ProtocolMessageEnum> extends BaseTypeHandler<T> {

    private static final String UNRECOGNIZED = "UNRECOGNIZED";

    private final T defaultEnum;
    private final T unrecognizedEnum;
    private final Map<String, T> enumNameMap;
    private final Map<Integer, T> enumNumberMap;

    public ProtobufEnumTypeHandler(Class<T> enumClass) {
        assert ProtocolMessageEnum.class.isAssignableFrom(enumClass)
                : "Not a ProtocolMessageEnum: " + enumClass.getName();
        assert enumClass.isEnum() : "Not an enum: " + enumClass.getName();
        this.enumNameMap = buildNameToEnumMap(enumClass);
        this.enumNumberMap = buildNumberToEnumMap(enumClass);
        this.defaultEnum = getDefaultEnum(enumClass);
        this.unrecognizedEnum = getUnrecognizedEnum(enumClass);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        switch (jdbcType) {
            case TINYINT, SMALLINT, INTEGER -> ps.setInt(i, parameter.getNumber());
            case BIGINT -> ps.setLong(i, parameter.getNumber());
            case FLOAT -> ps.setFloat(i, parameter.getNumber());
            case DOUBLE -> ps.setDouble(i, parameter.getNumber());
            case NUMERIC, DECIMAL -> ps.setBigDecimal(i, BigDecimal.valueOf(parameter.getNumber()));
            default -> ps.setString(i, parameter.name());
        }
    }

    @Override
    public @Nullable T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return fromObject(rs.getObject(columnName), rs.wasNull());
    }

    @Override
    public @Nullable T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return fromObject(rs.getObject(columnIndex), rs.wasNull());
    }

    @Override
    public @Nullable T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return fromObject(cs.getObject(columnIndex), cs.wasNull());
    }

    /**
     * Dynamically resolve a DB value (Number or String) to the corresponding enum constant.
     */
    private @Nullable T fromObject(@Nullable Object value, boolean wasNull) {
        if (wasNull || value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return enumNumberMap.getOrDefault(number.intValue(), unrecognizedEnum);
        }
        String str = value.toString().trim();
        if (str.isBlank()) {
            return defaultEnum;
        }
        // Try name lookup first, then fall back to numeric string
        T byName = enumNameMap.get(str);
        if (byName != null) {
            return byName;
        }
        if (isDigits(str)) {
            return enumNumberMap.getOrDefault(Integer.parseInt(str), unrecognizedEnum);
        }
        return unrecognizedEnum;
    }

    private static boolean isDigits(String s) {
        var len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    private T getDefaultEnum(Class<T> enumClass) {
        var enumConstants = enumClass.getEnumConstants();
        for (var e : enumConstants) {
            if (e.getNumber() == 0) {
                return e;
            }
        }
        throw new IllegalArgumentException(
                "Protobuf enum " + enumClass.getName() + " should have a default value with number 0");
    }

    private T getUnrecognizedEnum(Class<T> enumClass) {
        for (var e : enumClass.getEnumConstants()) {
            if (UNRECOGNIZED.equals(e.name())) {
                return e;
            }
        }
        throw new IllegalArgumentException(
                "Protobuf enum " + enumClass.getName() + " should have an UNRECOGNIZED value");
    }

    private Map<String, T> buildNameToEnumMap(Class<T> enumClass) {
        var result = new TreeMap<String, T>(String.CASE_INSENSITIVE_ORDER);
        var enumConstants = enumClass.getEnumConstants();
        if (enumConstants == null) {
            return result;
        }
        for (var e : enumConstants) {
            if (!UNRECOGNIZED.equals(e.name())) {
                result.put(e.name(), e);
            }
        }
        return result;
    }

    private Map<Integer, T> buildNumberToEnumMap(Class<T> enumClass) {
        var result = new LinkedHashMap<Integer, T>();
        var enumConstants = enumClass.getEnumConstants();
        if (enumConstants == null) {
            return result;
        }
        for (var e : enumConstants) {
            if (!UNRECOGNIZED.equals(e.name())) {
                result.put(e.getNumber(), e);
            }
        }
        return result;
    }
}
