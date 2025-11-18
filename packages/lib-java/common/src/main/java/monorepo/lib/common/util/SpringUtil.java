package monorepo.lib.common.util;

import org.springframework.context.ApplicationContext;

/**
 * @author Freeman
 * @since 2025/11/19
 */
public final class SpringUtil {

    private SpringUtil() {}

    private static ApplicationContext ctx;

    static void setContext(ApplicationContext applicationContext) {
        SpringUtil.ctx = applicationContext;
    }

    /**
     * Get the application context.
     *
     * @return the application context
     */
    public static ApplicationContext getContext() {
        if (ctx == null) {
            throw new IllegalStateException("You must in the Spring environment to use this method!");
        }
        return ctx;
    }
}
