package monorepo.lib.common.context;

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

        // 相同线程如果重复 set context 会导致执行 delegate 执行完之后清除 context
        // 造成 context 丢失
        if (ContextHolder.getOrNull() == context) {
            delegate.run();
            return;
        }

        ContextHolder.set(context);
        try {
            delegate.run();
        } finally {
            ContextHolder.remove();
        }
    }

    public static ContextualRunnable of(Runnable delegate) {
        if (delegate instanceof ContextualRunnable cr) {
            return cr;
        }
        return new ContextualRunnable(delegate);
    }
}
