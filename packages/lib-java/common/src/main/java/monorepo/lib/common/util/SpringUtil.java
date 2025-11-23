package monorepo.lib.common.util;

import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.util.function.SingletonSupplier;

import java.util.function.Supplier;

/**
 * @author Freeman
 * @since 2025/11/19
 */
public final class SpringUtil {

    private SpringUtil() {}

    @Nullable
    private static ApplicationContext ctx;
    private static final SingletonSupplier<TransactionOperations> transactionOperations = SingletonSupplier.of(() ->
            getContext().getBeanProvider(TransactionOperations.class).getIfUnique());

    static void setContext(ApplicationContext applicationContext) {
        SpringUtil.ctx = applicationContext;
    }

    /**
     * Get the application context.
     *
     * @return the application context
     */
    public static ApplicationContext getContext() {
        if (ctx == null) {
            throw new IllegalStateException("You must in the Spring environment to use this method!");
        }
        return ctx;
    }

    /**
     * Execute the supplier in transaction and return the result.
     *
     * @param supplier supplier
     * @param <T>     return type
     * @return result
     */
    public static <T> T withTransaction(Supplier<T> supplier) {
        var tx = transactionOperations.get();
        if (tx != null) {
            return tx.execute(_ -> supplier.get());
        } else {
            return supplier.get();
        }
    }

    /**
     * Execute the runnable in transaction.
     *
     * @param runnable runnable
     */
    public static void withTransaction(Runnable runnable) {
        var tx = transactionOperations.get();
        if (tx != null) {
            tx.executeWithoutResult(_ -> runnable.run());
        } else {
            runnable.run();
        }
    }
}
