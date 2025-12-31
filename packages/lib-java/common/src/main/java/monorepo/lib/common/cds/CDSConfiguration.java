package monorepo.lib.common.cds;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CDS configuration.
 *
 * @author Freeman
 * @since 2025/5/19
 */
@Configuration(proxyBeanMethods = false)
public class CDSConfiguration {

    @Bean
    public CDSTrainingRunHandler cdsTrainingRunHandler() {
        return new CDSTrainingRunHandler();
    }
}
