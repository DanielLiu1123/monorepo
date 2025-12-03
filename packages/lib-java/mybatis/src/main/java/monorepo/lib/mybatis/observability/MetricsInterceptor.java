package monorepo.lib.mybatis.observability;

import io.micrometer.core.instrument.MeterRegistry;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
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
public final class MetricsInterceptor implements Interceptor {

    private final MeterRegistry meterRegistry;

    public MetricsInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();

        long startTime = System.nanoTime();
        try {
            return invocation.proceed();
        } finally {
            long duration = System.nanoTime() - startTime;
            meterRegistry.timer("db.sql.execution", "db.statement", sql).record(duration, TimeUnit.NANOSECONDS);
        }
    }
}
