package monorepo.lib.common;

import monorepo.lib.common.json.JacksonConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/18
 */
@AutoConfiguration
@Import({JacksonConfiguration.class})
public class MonorepoAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(MonorepoAutoConfiguration.class);

    @Bean
    public Profile profile(Environment env) {
        String[] activeProfiles = env.getActiveProfiles();
        if (activeProfiles.length == 0) {
            return Profile.LOCAL;
        }
        if (activeProfiles.length > 1) {
            throw new IllegalStateException("Do NOT use multiple profiles, it makes unnecessary complexity!");
        }
        String profile = activeProfiles[0].toUpperCase();
        try {
            return Profile.valueOf(profile);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid profile '{}', defaulting to LOCAL", profile);
            return Profile.LOCAL;
        }
    }
}
