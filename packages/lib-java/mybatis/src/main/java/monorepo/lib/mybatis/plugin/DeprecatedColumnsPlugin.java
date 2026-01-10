package monorepo.lib.mybatis.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * A MyBatis Generator plugin that adds {@link Deprecated} annotations to model fields.
 *
 * <p> Example:
 * <pre>{@code
 * <plugin type="monorepo.lib.mybatis.plugin.DeprecatedColumnsPlugin"/>
 *
 * <table tableName="payment">
 *   <columnOverride column="is_prepaid">
 *       <property name="deprecated" value="true"/>
 *       <property name="deprecatedSince" value="2026-01-22"/>
 *       <property name="deprecatedForRemoval" value="false"/>
 *       <property name="deprecatedDescription" value="Use payment_status instead"/>
 *   </columnOverride>
 * </table>
 * }</pre>
 *
 * @author Freeman
 * @since 2026/1/10
 */
public class DeprecatedColumnsPlugin extends PluginAdapter {

    private static final String DEPRECATED = "deprecated";
    private static final String DEPRECATED_COLUMNS = "deprecatedSince";
    private static final String DEPRECATED_FOR_REMOVAL = "deprecatedForRemoval";
    private static final String DEPRECATED_DESCRIPTION = "deprecatedDescription";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelGetterMethodGenerated(
            Method method,
            TopLevelClass topLevelClass,
            IntrospectedColumn introspectedColumn,
            IntrospectedTable introspectedTable,
            ModelClassType modelClassType) {
        if (isColumnDeprecated(introspectedColumn)) {
            addDeprecatedAnnotation(method, introspectedColumn);
        }

        return true;
    }

    @Override
    public boolean modelSetterMethodGenerated(
            Method method,
            TopLevelClass topLevelClass,
            IntrospectedColumn introspectedColumn,
            IntrospectedTable introspectedTable,
            ModelClassType modelClassType) {
        if (isColumnDeprecated(introspectedColumn)) {
            addDeprecatedAnnotation(method, introspectedColumn);
        }

        return true;
    }

    @Override
    public boolean modelFieldGenerated(
            Field field,
            TopLevelClass topLevelClass,
            IntrospectedColumn introspectedColumn,
            IntrospectedTable introspectedTable,
            ModelClassType modelClassType) {
        if (isColumnDeprecated(introspectedColumn)) {
            addDeprecatedAnnotation(field, introspectedColumn);
        }

        return true;
    }

    private static boolean isColumnDeprecated(IntrospectedColumn introspectedColumn) {
        return Objects.equals(introspectedColumn.getProperties().getProperty(DEPRECATED), "true");
    }

    private static void addDeprecatedAnnotation(JavaElement javaElement, IntrospectedColumn introspectedColumn) {
        String since = introspectedColumn.getProperties().getProperty(DEPRECATED_COLUMNS);
        boolean forRemoval =
                Objects.equals(introspectedColumn.getProperties().getProperty(DEPRECATED_FOR_REMOVAL), "true");

        if (since == null && !forRemoval) {
            javaElement.addAnnotation("@Deprecated");
        } else {
            var params = new ArrayList<String>();
            if (since != null) {
                params.add("since = \"" + since + "\"");
            }
            if (forRemoval) {
                params.add("forRemoval = true");
            }
            String annotation = "@Deprecated(" + String.join(", ", params) + ")";
            javaElement.addAnnotation(annotation);
        }

        var description = introspectedColumn.getProperties().getProperty(DEPRECATED_DESCRIPTION);
        if (description != null) {
            List<String> javaDocLines = javaElement.getJavaDocLines();
            if (javaDocLines.isEmpty()) {
                javaElement.addJavaDocLine("/**");
                javaElement.addJavaDocLine(" * @deprecated " + description);
                javaElement.addJavaDocLine(" */");
            } else {
                int insertIndex = javaDocLines.size() - 1;
                String lastLine = javaDocLines.get(insertIndex).trim();
                var linesToInsert = new ArrayList<String>();
                linesToInsert.add(" * ");
                linesToInsert.add(" * @deprecated " + description);
                if ("*/".equals(lastLine)) {
                    javaDocLines.addAll(insertIndex, linesToInsert);
                } else {
                    javaDocLines.addAll(linesToInsert);
                }
            }
        }
    }
}
