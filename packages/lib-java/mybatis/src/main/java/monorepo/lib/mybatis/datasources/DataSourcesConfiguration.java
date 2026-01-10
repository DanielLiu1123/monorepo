package monorepo.lib.mybatis.datasources;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DataSourcesProperties.class)
public class DataSourcesConfiguration {

    @Bean
    static DataSourcesBeanDefinitionRegistry dataSourcesBeanDefinitionRegistry(Environment env) {
        return new DataSourcesBeanDefinitionRegistry(env);
    }
}
