package monorepo.lib.mybatis.datasources.dynamic;

import java.lang.reflect.Method;

/**
 * This interface is used to switch data sources dynamically.
 *
 * @author Freeman
 * @since 2026/1/10
 * @see <a href="https://en.wikipedia.org/wiki/Curiously_recurring_template_pattern">Curiously Recurring Template Pattern</a>
 */
public interface DynamicDataSource<T extends DynamicDataSource<T>> {

    Method useDataSourceMethod = getUseDataSourceMethod();

    /**
     * Return a new/cached instance with specified {@link javax.sql.DataSource}.
     *
     * @param dataSource dataSource name to use
     * @return new/cached instance with specified {@link javax.sql.DataSource}
     */
    @SuppressWarnings("unchecked")
    default T useDataSource(String dataSource) {
        return (T) this;
    }

    private static Method getUseDataSourceMethod() {
        try {
            return DynamicDataSource.class.getMethod("useDataSource", String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
