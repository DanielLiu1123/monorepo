package monorepo.lib.common.config;

import jakarta.annotation.Nullable;
import java.util.Optional;
import monorepo.lib.common.Profile;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Load default configurations.
 *
 * @author Freeman
 * @since 2025/11/18
 */
public final class DefaultConfigEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private final Log log;

    public DefaultConfigEnvironmentPostProcessor(DeferredLogFactory logFactory) {
        this.log = logFactory.getLog(getClass());
    }

    /**
     * Configurations to be loaded.
     */
    private static final String[] FILES = {"application-default-config.yaml"};

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        // profile-based configuration should be loaded before default configuration
        var profile = getProfile(environment);
        if (profile != null) {
            var configName =
                    switch (profile) {
                        case LOCAL -> "application-default-config-local.yaml";
                        case TEST -> "application-default-config-test.yaml";
                        case STAGING -> "application-default-config-staging.yaml";
                        case PROD -> "application-default-config-prod.yaml";
                    };
            var config = new ClassPathResource(configName);
            if (config.exists()) {
                environment.getPropertySources().addLast(loadProperties(config));
                log.info("Loaded profile-based configuration: " + config.getFilename());
            }
        }

        // load default configuration, which has the lowest priority than profile-based configuration
        for (String location : FILES) {
            ClassPathResource resource = new ClassPathResource(location);
            environment.getPropertySources().addLast(loadProperties(resource));
            log.info("Loaded default configuration: " + resource.getFilename());
        }
    }

    @Nullable
    private static Profile getProfile(ConfigurableEnvironment environment) {
        for (var activeProfile : environment.getActiveProfiles()) {
            for (var profile : Profile.values()) {
                if (profile.name().equalsIgnoreCase(activeProfile)) {
                    return profile;
                }
            }
        }
        return null;
    }

    private PropertySource<?> loadProperties(Resource resource) {
        String filename = Optional.ofNullable(resource.getFilename()).orElseThrow();
        if (!resource.exists()) {
            log.warn(filename + " doesn't exist");
        }

        // parse yaml
        var bean = new YamlPropertiesFactoryBean();
        bean.setResources(resource);

        var prop = Optional.ofNullable(bean.getObject()).orElseThrow();

        return new PropertiesPropertySource(filename, prop);
    }
}
