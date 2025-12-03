package monorepo.lib.mybatis;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import java.util.Optional;
import monorepo.lib.mybatis.observability.MetricsInterceptor;
import monorepo.lib.mybatis.observability.TraceInterceptor;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 *
 * @author Freeman
 * @since 2025/12/3
 */
@AutoConfiguration
@ConditionalOnClass(MybatisProperties.class)
public class MybatisAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(ObservationRegistry.class)
    static class Trace {
        @Bean
        public TraceInterceptor traceMyBatisInterceptor(Optional<ObservationRegistry> observationRegistry) {
            return new TraceInterceptor(observationRegistry.orElse(ObservationRegistry.NOOP));
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(MeterRegistry.class)
    static class Metrics {
        //        @Bean
        public MetricsInterceptor metricsMyBatisInterceptor(Optional<MeterRegistry> meterRegistry) {
            return new MetricsInterceptor(meterRegistry.orElseGet(CompositeMeterRegistry::new));
        }
    }
}
