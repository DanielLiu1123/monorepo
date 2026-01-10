package monorepo.lib.mybatis.datasources;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

/**
 * Used to parse {@code spring.datasource.additions} configuration
 * and dynamically register {@link HikariDataSource} Beans.
 *
 * @author Freeman
 * @since 2026/1/10
 */
public class DataSourcesBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor {

    private final Environment env;

    public DataSourcesBeanDefinitionRegistry(Environment env) {
        this.env = env;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        var properties = Binder.get(env).bindOrCreate(DataSourcesProperties.PREFIX, DataSourcesProperties.class);
        if (properties.additions().isEmpty()) {
            return;
        }

        var hikariConfig = Binder.get(env).bindOrCreate("spring.datasource.hikari", HikariConfig.class);

        for (var ds : properties.additions()) {
            var bd = BeanDefinitionBuilder.rootBeanDefinition(
                            HikariDataSource.class, () -> new HikariDataSource(ds.merge(hikariConfig)))
                    .getBeanDefinition();
            bd.setLazyInit(true);

            registry.registerBeanDefinition(ds.name(), bd);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {}
}
