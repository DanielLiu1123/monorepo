package monorepo.lib.mybatis.datasources;

import monorepo.lib.mybatis.datasources.dynamic.MyBatisDynamicDataSourceBeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration(proxyBeanMethods = false)
@ConditionalOnDataSources
@EnableConfigurationProperties(DataSourcesProperties.class)
public class DataSourcesConfiguration {

    @Bean
    static DataSourcesBeanDefinitionRegistry dataSourcesBeanDefinitionRegistry(Environment env) {
        return new DataSourcesBeanDefinitionRegistry(env);
    }

    @Bean
    static MyBatisDynamicDataSourceBeanPostProcessor myBatisDynamicDataSourceBeanPostProcessor(ApplicationContext ctx) {
        return new MyBatisDynamicDataSourceBeanPostProcessor(ctx);
    }
}
