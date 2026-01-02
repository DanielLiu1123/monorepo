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

    @Nullable private final Context context;

    @Nullable private final Observation parentObservation;

    private ContextualCallable(Callable<T> delegate) {
        this.delegate = delegate;
        this.context = ContextHolder.getOrNull();
        this.parentObservation = context != null ? context.observationRegistry().getCurrentObservation() : null;
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
            return ContextHolder.callWithContext(context, () -> withObservation(delegate));
        }
    }

    private T withObservation(Callable<T> callable) throws Exception {
        if (context == null) {
            return callable.call();
        }
        var ob = Observation.createNotStarted("async.callable", context.observationRegistry());
        if (parentObservation != null) {
            ob.parentObservation(parentObservation);
        }
        ob.start();
        try (var _ = ob.openScope()) {
            return callable.call();
        } catch (Throwable e) {
            ob.error(e);
            throw e;
        } finally {
            ob.stop();
        }
    }
}
