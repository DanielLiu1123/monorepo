package monorepo.lib.mybatis.plugin;

import java.util.List;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * <p> This plugin adds the {@code @Nullable} annotation to fields, getter methods, and setter method
 * parameters when the corresponding database column is nullable. This helps improve code quality by
 * providing explicit nullability information to static analysis tools and developers.
 *
 * <p> Usage:
 * <pre>{@code
 * <plugin type="monorepo.lib.mybatis.plugin.NullableColumnPlugin"/>
 * }</pre>
 *
 * <p> Generated code example:
 * <pre>{@code
 * public class User {
 *     private String username; // NOT NULL column in database
 *
 *     @Nullable
 *     private String bio;      // nullable column in database
 *
 *     // For a NOT NULL column - no annotation needed
 *     public String getUsername() {
 *         return username;
 *     }
 *
 *     public void setUsername(String username) {
 *         this.username = username;
 *     }
 *
 *     // For a nullable column - @Nullable annotation added to field, getter, and setter parameter
 *     @Nullable
 *     public String getBio() {
 *         return bio;
 *     }
 *
 *     public void setBio(@Nullable String bio) {
 *         this.bio = bio;
 *     }
 * }
 * }</pre>
 *
 * <p> The plugin uses {@code org.jspecify.annotations.Nullable}.
 *
 * @author Freeman
 * @since 2025/4/26
 */
public class NullableColumnPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelFieldGenerated(
            Field field,
            TopLevelClass topLevelClass,
            IntrospectedColumn introspectedColumn,
            IntrospectedTable introspectedTable,
            ModelClassType modelClassType) {

        if (introspectedColumn.isNullable()) {
            // Add import for the annotation
            topLevelClass.addImportedType("org.jspecify.annotations.Nullable");

            // Add the annotation to the field
            field.addAnnotation("@Nullable");
        }

        return true;
    }

    @Override
    public boolean modelGetterMethodGenerated(
            Method method,
            TopLevelClass topLevelClass,
            IntrospectedColumn introspectedColumn,
            IntrospectedTable introspectedTable,
            ModelClassType modelClassType) {

        if (introspectedColumn.isNullable()) {
            // Add import for the annotation
            topLevelClass.addImportedType("org.jspecify.annotations.Nullable");

            // Add the annotation to the method
            method.addAnnotation("@Nullable");
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

        if (introspectedColumn.isNullable()) {
            // Add import for the annotation
            topLevelClass.addImportedType("org.jspecify.annotations.Nullable");

            // Add the annotation to the setter method parameter
            List<Parameter> parameters = method.getParameters();
            if (!parameters.isEmpty()) {
                Parameter parameter = parameters.getFirst();
                parameter.addAnnotation("@Nullable");
            }
        }

        return true;
    }
}
