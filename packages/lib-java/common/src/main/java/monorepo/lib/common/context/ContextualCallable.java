package monorepo.lib.common.context;

import java.util.concurrent.Callable;
import org.jspecify.annotations.Nullable;

/**
 * @author Freeman
 * @since 2025/5/1
 */
public final class ContextualCallable<T> implements Callable<T> {

    private final Callable<T> delegate;

    @Nullable
    private final Context context;

    private ContextualCallable(Callable<T> delegate) {
        this.delegate = delegate;
        this.context = ContextHolder.getOrNull();
    }

    @Override
    public T call() throws Exception {

        // If the same thread repeatedly sets the context, it will cause the context to be cleared after the delegate
        // executes,
        // resulting in context loss. First set, last clear.
        if (ContextHolder.getOrNull() == context) {
            return delegate.call();
        }

        ContextHolder.set(context);
        try {
            return delegate.call();
        } finally {
            ContextHolder.remove();
        }
    }

    public static <T> ContextualCallable<T> of(Callable<T> delegate) {
        if (delegate instanceof ContextualCallable<T> cc) {
            return cc;
        }
        return new ContextualCallable<>(delegate);
    }
}
