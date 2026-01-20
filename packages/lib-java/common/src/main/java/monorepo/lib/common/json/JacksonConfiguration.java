package monorepo.lib.common.json;

import jacksonmodule.protobuf.v3.ProtobufModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson configuration.
 *
 * @author Freeman
 * @since 2025/11/18
 */
@Configuration(proxyBeanMethods = false)
public class JacksonConfiguration {

    @Bean
    public BigNumberModule bigNumberJacksonModule() {
        return new BigNumberModule();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(ProtobufModule.class)
    static class ProtobufModuleConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ProtobufModule protobufJacksonModule() {
            return new ProtobufModule();
        }
    }
}
