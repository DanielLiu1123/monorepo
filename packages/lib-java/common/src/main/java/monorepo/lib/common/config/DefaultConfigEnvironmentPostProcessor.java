package monorepo.lib.common.config;

import jakarta.annotation.Nullable;

import java.time.ZoneOffset;
import java.util.Optional;
import java.util.TimeZone;

import monorepo.lib.common.profile.Profile;
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

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        // we always use UTC time zone for less confusion
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));

        // profile-based configuration should be loaded before default configuration
        var profile = getProfileOrNull(environment);
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
        ClassPathResource resource = new ClassPathResource("application-default-config.yaml");
        environment.getPropertySources().addLast(loadProperties(resource));
        log.info("Loaded default configuration: " + resource.getFilename());
    }

    @Nullable
    private static Profile getProfileOrNull(ConfigurableEnvironment environment) {
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
