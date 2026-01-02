package monorepo.lib.common.context;

import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

/**
 * Context holder for request.
 *
 * @author Freeman
 * @since 2025/11/19
 */
public final class ContextHolder {

    private ContextHolder() {}

    private static final ThreadLocal<Context> CONTEXT = new ThreadLocal<>();

    public static Context get() {
        var ctx = getOrNull();
        if (ctx == null) {
            throw new IllegalStateException("Context is not initialized");
        }
        return ctx;
    }

    @Nullable public static Context getOrNull() {
        return CONTEXT.get();
    }

    public static <T> T getWithContext(Context context, Supplier<T> supplier) {
        var previous = getOrNull();
        set(context);
        try {
            return supplier.get();
        } finally {
            if (previous != null) {
                set(previous);
            } else {
                remove();
            }
        }
    }

    public static <T> T callWithContext(Context context, Callable<T> callable) throws Exception {
        var previous = getOrNull();
        set(context);
        try {
            return callable.call();
        } finally {
            if (previous != null) {
                set(previous);
            } else {
                remove();
            }
        }
    }

    public static void runWithContext(Context context, Runnable runnable) {
        var previous = getOrNull();
        set(context);
        try {
            runnable.run();
        } finally {
            if (previous != null) {
                set(previous);
            } else {
                remove();
            }
        }
    }

    private static void set(Context ctx) {
        CONTEXT.set(ctx);
    }

    private static void remove() {
        CONTEXT.remove();
    }
}
