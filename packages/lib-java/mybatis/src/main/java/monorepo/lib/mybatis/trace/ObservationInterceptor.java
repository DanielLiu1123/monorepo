package monorepo.lib.mybatis.trace;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import java.sql.Statement;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;

/**
 * MyBatis interceptor that creates observations for SQL execution.
 * Records SQL statements in traces using Micrometer Observation API.
 *
 * @author Freeman
 * @since 2025/12/3
 */
@Intercepts({
    @Signature(
            type = StatementHandler.class,
            method = "query",
            args = {Statement.class, ResultHandler.class}),
    @Signature(
            type = StatementHandler.class,
            method = "update",
            args = {Statement.class}),
    @Signature(
            type = StatementHandler.class,
            method = "batch",
            args = {Statement.class})
})
public final class ObservationInterceptor implements Interceptor {

    private final ObservationRegistry observationRegistry;

    public ObservationInterceptor(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();

        // Extract mapped statement id
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        String statementId = mappedStatement.getId();
        String operationType = invocation.getMethod().getName();

        // Create observation with SQL details
        Observation observation = Observation.createNotStarted("mybatis.sql", observationRegistry)
                .lowCardinalityKeyValue("db.system", "sql")
                .lowCardinalityKeyValue("db.operation", operationType)
                .lowCardinalityKeyValue("db.sql.table", extractTableName(sql))
                .highCardinalityKeyValue("db.statement", sql)
                .highCardinalityKeyValue("mybatis.statement.id", statementId);

        observation.start();

        try (var _ = observation.openScope()) {
            return invocation.proceed();
        } catch (Throwable t) {
            observation.error(t);
            throw t;
        } finally {
            observation.stop();
        }
    }

    /**
     * Extract table name from SQL statement for better tracing.
     */
    private String extractTableName(String sql) {
        if (sql == null || sql.isEmpty()) {
            return "unknown";
        }

        String upperSql = sql.toUpperCase().trim();
        String tableName = "unknown";

        try {
            if (upperSql.startsWith("SELECT")) {
                int fromIndex = upperSql.indexOf("FROM");
                if (fromIndex > 0) {
                    String afterFrom = sql.substring(fromIndex + 4).trim();
                    tableName = afterFrom.split("\\s+")[0];
                }
            } else if (upperSql.startsWith("INSERT")) {
                int intoIndex = upperSql.indexOf("INTO");
                if (intoIndex > 0) {
                    String afterInto = sql.substring(intoIndex + 4).trim();
                    tableName = afterInto.split("\\s+")[0];
                }
            } else if (upperSql.startsWith("UPDATE")) {
                String afterUpdate = sql.substring(6).trim();
                tableName = afterUpdate.split("\\s+")[0];
            } else if (upperSql.startsWith("DELETE")) {
                int fromIndex = upperSql.indexOf("FROM");
                if (fromIndex > 0) {
                    String afterFrom = sql.substring(fromIndex + 4).trim();
                    tableName = afterFrom.split("\\s+")[0];
                }
            }
        } catch (Exception e) {
            // If parsing fails, return unknown
            return "unknown";
        }

        return tableName;
    }
}
