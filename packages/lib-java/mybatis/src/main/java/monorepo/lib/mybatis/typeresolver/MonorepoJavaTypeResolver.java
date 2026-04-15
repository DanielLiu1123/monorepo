package monorepo.lib.mybatis.typeresolver;

import java.sql.Types;
import java.time.Instant;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

public final class MonorepoJavaTypeResolver extends JavaTypeResolverDefaultImpl {

    @Override
    protected JdbcTypeInformation overrideDefault(
            IntrospectedColumn column, JdbcTypeInformation defaultTypeInformation) {
        return switch (column.getJdbcType()) {
            case Types.TIMESTAMP ->
                defaultTypeInformation.withJavaType(new FullyQualifiedJavaType(Instant.class.getName()));
            case Types.TINYINT, Types.SMALLINT ->
                defaultTypeInformation.withJavaType(new FullyQualifiedJavaType(Integer.class.getName()));
            default -> super.overrideDefault(column, defaultTypeInformation);
        };
    }
}
