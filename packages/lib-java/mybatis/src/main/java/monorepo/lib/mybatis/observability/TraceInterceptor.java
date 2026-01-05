package monorepo.lib.mybatis.observability;

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
public final class TraceInterceptor implements Interceptor {

    private final ObservationRegistry observationRegistry;

    public TraceInterceptor(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        String operationType = invocation.getMethod().getName();

        Observation observation = Observation.createNotStarted("db.sql." + operationType, observationRegistry)
                .highCardinalityKeyValue("db.statement", sql);

        return observation.observeChecked(invocation::proceed);
    }
}
