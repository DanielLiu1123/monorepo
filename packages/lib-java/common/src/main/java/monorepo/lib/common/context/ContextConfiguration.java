package monorepo.lib.common.context;

import io.grpc.ClientInterceptor;
import io.grpc.ServerInterceptor;
import io.micrometer.context.ContextRegistry;
import monorepo.lib.common.context.grpc.ContextualClientInterceptor;
import monorepo.lib.common.context.grpc.ContextualServerInterceptor;
import monorepo.lib.common.context.restclient.ContextualClientHttpRequestInterceptor;
import monorepo.lib.common.context.webmv.ContextualOncePerRequestFilter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author Freeman
 * @since 2025/11/19
 */
@Configuration(proxyBeanMethods = false)
public class ContextConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        ContextRegistry.getInstance().registerThreadLocalAccessor(new ContextThreadLocalAccessor());
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication
    @ConditionalOnClass({OncePerRequestFilter.class})
    static class WebMvcServer {
        @Bean
        public ContextualOncePerRequestFilter contextualOncePerRequestFilter() {
            return new ContextualOncePerRequestFilter();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(RestClientCustomizer.class)
    static class RestClient {
        @Bean
        public RestClientCustomizer contextualRestClientCustomizer() {
            return builder -> builder.requestInterceptor(new ContextualClientHttpRequestInterceptor());
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(ServerInterceptor.class)
    static class GrpcServer {
        @Bean
        public ContextualServerInterceptor contextualServerInterceptor() {
            return new ContextualServerInterceptor();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(ClientInterceptor.class)
    static class GrpcClient {
        @Bean
        public ContextualClientInterceptor contextualClientInterceptor() {
            return new ContextualClientInterceptor();
        }
    }
}
