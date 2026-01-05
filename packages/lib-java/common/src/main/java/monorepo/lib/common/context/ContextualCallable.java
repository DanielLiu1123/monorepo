package monorepo.lib.common.context;

import io.micrometer.observation.Observation;
import java.util.concurrent.Callable;
import org.jspecify.annotations.Nullable;

/**
 * @author Freeman
 * @since 2025/5/1
 */
final class ContextualCallable<T> implements Callable<T> {

    private final Callable<T> delegate;

    private final @Nullable Context context;

    private final @Nullable Observation parentObservation;

    private final String parentThreadName;

    private ContextualCallable(Callable<T> delegate) {
        this.delegate = delegate;
        this.context = ContextHolder.getOrNull();
        this.parentObservation = context != null ? context.observationRegistry().getCurrentObservation() : null;
        this.parentThreadName = Thread.currentThread().getName();
    }

    public static <T> ContextualCallable<T> of(Callable<T> delegate) {
        if (delegate instanceof ContextualCallable<T> cc) {
            return cc;
        }
        return new ContextualCallable<>(delegate);
    }

    @Override
    public T call() throws Exception {
        if (context == null) {
            return delegate.call();
        } else {
            return ContextHolder.callWithContext(context, this::withObservation);
        }
    }

    private T withObservation() throws Exception {
        if (context == null) {
            return delegate.call();
        }
        var observation = Observation.createNotStarted("async.callable", context.observationRegistry());
        observation.highCardinalityKeyValue("thread.caller", parentThreadName);
        observation.highCardinalityKeyValue(
                "thread.current", Thread.currentThread().getName());
        if (parentObservation != null) {
            observation.parentObservation(parentObservation);
        }
        return observation.observeChecked(delegate::call);
    }
}
