package monorepo.lib.core;

import monorepo.lib.core.cds.CDSConfiguration;
import monorepo.lib.core.context.ContextConfiguration;
import monorepo.lib.core.json.JacksonConfiguration;
import monorepo.lib.core.profile.ProfileConfiguration;
import monorepo.lib.core.util.UtilConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * Common auto-configuration.
 *
 * @author Freeman
 * @since 2025/11/18
 */
@AutoConfiguration
@Import({
    CDSConfiguration.class,
    ContextConfiguration.class,
    JacksonConfiguration.class,
    ProfileConfiguration.class,
    UtilConfiguration.class
})
public class MonorepoAutoConfiguration {}
