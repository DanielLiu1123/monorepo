package monorepo.lib.mybatis;

import io.micrometer.observation.ObservationRegistry;
import java.util.Optional;
import monorepo.lib.mybatis.trace.ObservationInterceptor;
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
public class MybatisAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(ObservationRegistry.class)
    static class Trace {
        @Bean
        public ObservationInterceptor observationMyBatisInterceptor(Optional<ObservationRegistry> observationRegistry) {
            return new ObservationInterceptor(observationRegistry.orElse(ObservationRegistry.NOOP));
        }
    }
}
