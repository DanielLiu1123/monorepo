package monorepo.lib.common.context;

import io.micrometer.observation.Observation;
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

        if (context == null) {
            return delegate.call();
        }

        var observation = Observation.createNotStarted("contextual.call", context.observationRegistry())
                .start();
        try {
            // If the same thread repeatedly sets the context, it will cause the context to be cleared after the
            // delegate
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
        } finally {
            observation.stop();
        }
    }

    public static <T> ContextualCallable<T> of(Callable<T> delegate) {
        if (delegate instanceof ContextualCallable<T> cc) {
            return cc;
        }
        return new ContextualCallable<>(delegate);
    }
}
