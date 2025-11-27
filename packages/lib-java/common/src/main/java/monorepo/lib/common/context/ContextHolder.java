package monorepo.lib.common.context;

import org.jspecify.annotations.Nullable;

/**
 * @author Freeman
 * @since 2025/11/19
 */
public final class ContextHolder {

    private static final ThreadLocal<Context> CONTEXT = new ThreadLocal<>();

    public static Context get() {
        var ctx = getOrNull();
        if (ctx == null) {
            throw new IllegalStateException("Context is not initialized");
        }
        return ctx;
    }

    @Nullable
    public static Context getOrNull() {
        return CONTEXT.get();
    }

    public static void set(Context ctx) {
        CONTEXT.set(ctx);
    }

    public static void remove() {
        CONTEXT.remove();
    }
}
