package monorepo.lib.common.json;

import jacksonmodule.protobuf.v3.ProtobufModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/18
 */
@Configuration(proxyBeanMethods = false)
public class JacksonConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ProtobufModule protobufJacksonModuleModule() {
        return new ProtobufModule();
    }

    @Bean
    public BigNumberModule bigNumberJacksonModule() {
        return new BigNumberModule();
    }
}
