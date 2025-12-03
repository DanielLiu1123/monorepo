package monorepo.lib.mybatis.trace;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import java.sql.Statement;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
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

        Observation observation = Observation.createNotStarted("db.sql", observationRegistry)
                .highCardinalityKeyValue("db.statement", sql);

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
}
