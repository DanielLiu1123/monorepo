package monorepo.lib.common.context;

import io.micrometer.observation.Observation;
import org.jspecify.annotations.Nullable;

/**
 * @author Freeman
 * @since 2025/5/1
 */
public final class ContextualRunnable implements Runnable {

    private final Runnable delegate;

    @Nullable
    private final Context context;

    @Nullable
    private final Observation parentObservation;

    private ContextualRunnable(Runnable delegate) {
        this.delegate = delegate;
        this.context = ContextHolder.getOrNull();
        this.parentObservation = context != null ? context.observationRegistry().getCurrentObservation() : null;
    }

    public static ContextualRunnable of(Runnable delegate) {
        if (delegate instanceof ContextualRunnable cr) {
            return cr;
        }
        return new ContextualRunnable(delegate);
    }

    @Override
    public void run() {
        if (context == null) {
            delegate.run();
            return;
        }

        // If the same thread repeatedly sets the context, it will cause the context to be cleared after the
        // delegate executes, resulting in context loss. First set, last clear.
        if (ContextHolder.getOrNull() == context) {
            withObservation(delegate);
            return;
        }

        ContextHolder.set(context);
        try {
            withObservation(delegate);
        } finally {
            ContextHolder.remove();
        }
    }

    private void withObservation(Runnable runnable) {
        if (context == null) {
            runnable.run();
            return;
        }
        var ob = Observation.createNotStarted("async.runnable", this.context.observationRegistry());
        if (parentObservation != null) {
            ob.parentObservation(parentObservation);
        }
        ob.start();
        try (var _ = ob.openScope()) {
            runnable.run();
        } catch (Throwable e) {
            ob.error(e);
            throw e;
        } finally {
            ob.stop();
        }
    }
}
