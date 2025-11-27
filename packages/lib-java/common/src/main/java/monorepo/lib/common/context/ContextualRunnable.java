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

    private ContextualRunnable(Runnable delegate) {
        this.delegate = delegate;
        this.context = ContextHolder.getOrNull();
    }

    @Override
    public void run() {

        if (context == null) {
            delegate.run();
            return;
        }

        var observation = Observation.createNotStarted("contextual.run", context.observationRegistry())
                .start();

        try {
            // If the same thread repeatedly sets the context, it will cause the context to be cleared after the
            // delegate
            // executes,
            // resulting in context loss. First set, last clear.
            if (ContextHolder.getOrNull() == context) {
                delegate.run();
            } else {
                ContextHolder.set(context);
                try {
                    delegate.run();
                } finally {
                    ContextHolder.remove();
                }
            }
        } finally {
            observation.stop();
        }
    }

    public static ContextualRunnable of(Runnable delegate) {
        if (delegate instanceof ContextualRunnable cr) {
            return cr;
        }
        return new ContextualRunnable(delegate);
    }
}
