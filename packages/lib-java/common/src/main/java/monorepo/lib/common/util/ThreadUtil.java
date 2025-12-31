package monorepo.lib.common.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import monorepo.lib.common.context.ContextualExecutorService;

/**
 * Utility for thread.
 *
 * @author Freeman
 * @since 2025/11/18
 */
public final class ThreadUtil {
    private ThreadUtil() {}

    private static final ExecutorService executorService =
            new ContextualExecutorService(Executors.newVirtualThreadPerTaskExecutor());

    /**
     * Get the shared executor service.
     *
     * @return the shared executor service
     */
    public static ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Run a task asynchronously using the shared executor service.
     *
     * @param runnable the runnable task
     * @return a CompletableFuture representing the asynchronous execution
     */
    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, executorService);
    }

    /**
     * Supply a value asynchronously using the shared executor service.
     *
     * @param supplier the supplier
     * @param <T>      the type
     * @return a CompletableFuture representing the asynchronous execution
     */
    public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, executorService);
    }
}
