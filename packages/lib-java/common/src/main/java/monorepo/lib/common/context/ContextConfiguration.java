package monorepo.lib.common.context;

import io.grpc.ClientInterceptor;
import io.grpc.ServerInterceptor;
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
    @ConditionalOnClass(ServerInterceptor.class)
    static class GrpcServer {
        @Bean
        @Order(0)
        public ServerInterceptor observationGrpcServerInterceptor(Optional<ObservationRegistry> observationRegistry) {
            return new ObservationGrpcServerInterceptor(observationRegistry.orElse(ObservationRegistry.NOOP));
        }

        @Bean
        public ServerInterceptor contextualServerInterceptor(Optional<ObservationRegistry> observationRegistry) {
            return new ContextualServerInterceptor(observationRegistry.orElse(ObservationRegistry.NOOP));
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(ClientInterceptor.class)
    static class GrpcClient {
        @Bean
        @Order(0)
        public ObservationGrpcClientInterceptor observationGrpcClientInterceptor(
                Optional<ObservationRegistry> observationRegistry) {
            return new ObservationGrpcClientInterceptor(observationRegistry.orElse(ObservationRegistry.NOOP));
        }

        @Bean
        public ContextualClientInterceptor contextualClientInterceptor() {
            return new ContextualClientInterceptor();
        }
    }
}
