package monorepo.lib.mybatis.typehandler;

import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import monorepo.lib.core.util.JsonUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.jspecify.annotations.Nullable;
import org.postgresql.util.PGobject;
import org.springframework.core.ParameterizedTypeReference;

/**
 * Converts Postgres jsonb type to Java objects using {@link JsonUtil}.
 *
 * @author Freeman
 * @since 2025/11/22
 */
public final class JsonbTypeHandler<T> extends BaseTypeHandler<T> {

    private final Type type;

    public JsonbTypeHandler(Type type) {
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        var obj = new PGobject();
        obj.setType("jsonb");
        obj.setValue(JsonUtil.stringify(parameter));
        ps.setObject(i, obj);
    }

    @Override
    public @Nullable T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return fromJson(rs.getString(columnName));
    }

    @Override
    public @Nullable T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return fromJson(rs.getString(columnIndex));
    }

    @Override
    public @Nullable T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return fromJson(cs.getString(columnIndex));
    }

    private @Nullable T fromJson(@Nullable String value) {
        if (value == null) {
            return null;
        }
        return JsonUtil.parse(value, ParameterizedTypeReference.forType(type));
    }
}
