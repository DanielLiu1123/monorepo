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
        this.context = ContextHolder.get();
    }

    @Override
    public T call() throws Exception {

        // 相同线程如果重复 set context 会导致执行 delegate 执行完之后清除 context
        // 造成 context 丢失
        if (ContextHolder.get() == context) {
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
