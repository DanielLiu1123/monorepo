package monorepo.lib.common.context;

import io.micrometer.observation.Observation;
import org.jspecify.annotations.Nullable;

/**
 * @author Freeman
 * @since 2025/5/1
 */
final class ContextualRunnable implements Runnable {

    private final Runnable delegate;

    private final @Nullable Context context;

    private final @Nullable Observation parentObservation;

    private final String parentThreadName;

    private ContextualRunnable(Runnable delegate) {
        this.delegate = delegate;
        this.context = ContextHolder.getOrNull();
        this.parentObservation = context != null ? context.observationRegistry().getCurrentObservation() : null;
        this.parentThreadName = Thread.currentThread().getName();
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
        } else {
            ContextHolder.runWithContext(context, this::withObservation);
        }
    }

    private void withObservation() {
        if (context == null) {
            delegate.run();
            return;
        }
        var observation = Observation.createNotStarted("async.runnable", this.context.observationRegistry());
        observation.highCardinalityKeyValue("thread.caller", parentThreadName);
        observation.highCardinalityKeyValue(
                "thread.current", Thread.currentThread().getName());
        if (parentObservation != null) {
            observation.parentObservation(parentObservation);
        }
        observation.observe(delegate);
    }
}
