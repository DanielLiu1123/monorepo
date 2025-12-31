package monorepo.lib.common;

import monorepo.lib.common.cds.CDSConfiguration;
import monorepo.lib.common.context.ContextConfiguration;
import monorepo.lib.common.json.JacksonConfiguration;
import monorepo.lib.common.profile.ProfileConfiguration;
import monorepo.lib.common.util.UtilConfiguration;
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
