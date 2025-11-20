package monorepo.lib.common.context;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/20
 */
public final class ContextConsts {
    private ContextConsts() {}

    /**
     * @see <a href="https://www.w3.org/TR/2021/REC-trace-context-1-20211123/#design-overview">Trace Context</a>
     */
    public static final Set<String> opentelemetryHeaders = Set.of(
            "traceparent",
            "tracestate"
    );

    public static final Set<BiPredicate<String, List<String>>> propagatedHeaders = Set.of(
            (key, _) -> "Authorization".equalsIgnoreCase(key),
            // opentelemetry traces headers
            (key, _) -> opentelemetryHeaders.contains(key)
    );
}
