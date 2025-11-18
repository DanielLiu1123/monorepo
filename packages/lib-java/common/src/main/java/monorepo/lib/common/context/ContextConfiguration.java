package monorepo.lib.common.context;

import monorepo.lib.common.context.web.ContextualOncePerRequestFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author Freeman
 * @since 2025/5/1
 */
@Configuration(proxyBeanMethods = false)
public class ContextConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication
    @ConditionalOnClass({OncePerRequestFilter.class})
    static class Web {

        @Bean
        public ContextualOncePerRequestFilter contextualOncePerRequestFilter() {
            return new ContextualOncePerRequestFilter();
        }
    }
}
