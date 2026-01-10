package monorepo.lib.mybatis.datasources;

import monorepo.lib.mybatis.datasources.dynamic.MyBatisDynamicDataSourceBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DataSourcesProperties.class)
@Conditional(DataSourcesConfiguration.MultipleDataSourcesCondition.class)
public class DataSourcesConfiguration {

    @Bean
    static DataSourcesBeanDefinitionRegistry dataSourcesBeanDefinitionRegistry(Environment env) {
        return new DataSourcesBeanDefinitionRegistry(env);
    }

    @Bean
    static MyBatisDynamicDataSourceBeanPostProcessor myBatisDynamicDataSourceBeanPostProcessor(ApplicationContext ctx) {
        return new MyBatisDynamicDataSourceBeanPostProcessor(ctx);
    }

    static final class MultipleDataSourcesCondition extends SpringBootCondition {
        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            var properties = Binder.get(context.getEnvironment())
                    .bindOrCreate(DataSourcesProperties.PREFIX, DataSourcesProperties.class);
            var configured = !properties.datasources().isEmpty();
            if (configured) {
                return ConditionOutcome.match("Multiple data sources configured.");
            } else {
                return ConditionOutcome.noMatch("No multiple data sources configured.");
            }
        }
    }
}
