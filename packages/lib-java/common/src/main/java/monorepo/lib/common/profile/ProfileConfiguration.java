package monorepo.lib.common.profile;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Profile configuration.
 *
 * @author Freeman
 * @since 2025/11/19
 */
@Configuration(proxyBeanMethods = false)
public class ProfileConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ProfileConfiguration.class);

    @Bean
    public Profile profile(Environment env) {
        String[] activeProfiles = env.getActiveProfiles();
        if (activeProfiles.length == 0) {
            return Profile.LOCAL;
        }
        if (activeProfiles.length > 1) {
            throw new IllegalStateException("Do NOT use multiple profiles, it makes unnecessary complexity!");
        }
        String profile = activeProfiles[0].toUpperCase(Locale.ROOT);
        try {
            return Profile.valueOf(profile);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid profile '{}', defaulting to LOCAL", profile);
            return Profile.LOCAL;
        }
    }
}
