package monorepo.lib.mybatis.plugin;

import java.util.List;
import monorepo.lib.mybatis.datasources.dynamic.DynamicDataSource;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;

/**
 * Make MyBatis mapper interfaces implement {@link DynamicDataSource}.
 *
 * <p> Usage:
 * <pre>{@code
 * <plugin type="monorepo.lib.mybatis.plugin.DynamicDataSourcePlugin"/>
 * }</pre>
 *
 * <p> Generated code:
 * <pre>{@code
 * public interface TodoMapper extends DynamicDataSource<TodoMapper> {
 *     // ...
 * }
 * }</pre>
 *
 * @author Freeman
 * @since 2026/1/10
 */
public class DynamicDataSourcePlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        var baseType = new FullyQualifiedJavaType(
                "DynamicDataSource<" + interfaze.getType().getShortName() + ">");
        interfaze.addImportedType(
                new FullyQualifiedJavaType("monorepo.lib.mybatis.datasources.dynamic.DynamicDataSource"));
        interfaze.addSuperInterface(baseType);
        return true;
    }
}
