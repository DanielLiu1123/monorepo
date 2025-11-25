package monorepo.lib.common.context;

import io.micrometer.context.ThreadLocalAccessor;
import org.jspecify.annotations.Nullable;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/26
 */
public final class ContextThreadLocalAccessor implements ThreadLocalAccessor<Context> {

    public static final String KEY = ContextThreadLocalAccessor.class.getName() + ".KEY";

    @Override
    public Object key() {
        return KEY;
    }

    @Override
    public @Nullable Context getValue() {
        return ContextHolder.getOrNull();
    }

    @Override
    public void setValue(Context value) {
        ContextHolder.set(value);
    }

    @Override
    public void setValue() {
        ContextHolder.remove();
    }
}
