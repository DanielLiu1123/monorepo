package monorepo.lib.mybatis.typehandler;

import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import monorepo.lib.common.util.JsonUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.jspecify.annotations.Nullable;
import org.postgresql.util.PGobject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;

/**
 * Converts Postgres jsonb type to Java objects using {@link JsonUtil}.
 *
 * <p> Example:
 * <pre>{@code
 * record Person(String name, int age) {}
 *
 * public class PersonTypeHandler extends BasePostgresJsonbTypeHandler<Person> {
 * }
 * }</pre>
 *
 * @author Freeman
 * @since 2025/11/22
 */
public abstract class BasePostgresJsonbTypeHandler<T> extends BaseTypeHandler<T> {

    private final Type type;

    protected BasePostgresJsonbTypeHandler() {
        this.type = ResolvableType.forClass(getClass())
                .as(BasePostgresJsonbTypeHandler.class)
                .getGeneric(0)
                .getType();
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

    @Nullable
    private T fromJson(@Nullable String value) {
        if (value == null) {
            return null;
        }
        return JsonUtil.parse(value, ParameterizedTypeReference.forType(type));
    }
}
