package monorepo.lib.common;

import monorepo.lib.common.json.JacksonConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/18
 */
@AutoConfiguration
@Import({JacksonConfiguration.class})
public class MonoAutoConfiguration {}
