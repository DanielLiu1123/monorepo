package monorepo.lib.common.context;

import grpcstarter.client.ConditionOnGrpcClientEnabled;
import grpcstarter.server.ConditionOnGrpcServerEnabled;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.ObservationRegistry;
import java.util.Optional;
import monorepo.lib.common.context.grpc.ContextualClientInterceptor;
import monorepo.lib.common.context.grpc.ContextualServerInterceptor;
import monorepo.lib.common.context.restclient.ContextualClientHttpRequestInterceptor;
import monorepo.lib.common.context.webmvc.ContextualOncePerRequestFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration(proxyBeanMethods = false)
public class ContextConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication
    @ConditionalOnClass({OncePerRequestFilter.class})
    static class WebMvcServer {
        @Bean
        public ContextualOncePerRequestFilter contextualOncePerRequestFilter(
                Optional<ObservationRegistry> observationRegistry) {
            return new ContextualOncePerRequestFilter(observationRegistry.orElse(ObservationRegistry.NOOP));
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
    @ConditionOnGrpcServerEnabled
    static class GrpcServer {
        @Bean
        public ContextualServerInterceptor contextualServerInterceptor(
                Optional<ObservationRegistry> observationRegistry) {
            return new ContextualServerInterceptor(observationRegistry.orElse(ObservationRegistry.NOOP));
        }

        @Configuration(proxyBeanMethods = false)
        @ConditionalOnClass(ObservationGrpcServerInterceptor.class) // micrometer exists
        static class Micrometer {
            @Bean
            @Order(-100000) // relative high precedence
            public ObservationGrpcServerInterceptor observationGrpcServerInterceptor(
                    Optional<ObservationRegistry> observationRegistry) {
                return new ObservationGrpcServerInterceptor(observationRegistry.orElse(ObservationRegistry.NOOP));
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionOnGrpcClientEnabled
    static class GrpcClient {
        @Bean
        public ContextualClientInterceptor contextualClientInterceptor() {
            return new ContextualClientInterceptor();
        }

        @Configuration(proxyBeanMethods = false)
        @ConditionalOnClass(ObservationGrpcClientInterceptor.class) // micrometer exists
        static class Micrometer {
            @Bean
            @Order(-100000) // relative high precedence
            public ObservationGrpcClientInterceptor observationGrpcClientInterceptor(
                    Optional<ObservationRegistry> observationRegistry) {
                return new ObservationGrpcClientInterceptor(observationRegistry.orElse(ObservationRegistry.NOOP));
            }
        }
    }
}
