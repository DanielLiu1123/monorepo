package monorepo.lib.common.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/19
 */
@Configuration(proxyBeanMethods = false)
public class UtilConfiguration {
    public UtilConfiguration(ApplicationContext applicationContext) {
        SpringUtil.setContext(applicationContext);
    }
}
