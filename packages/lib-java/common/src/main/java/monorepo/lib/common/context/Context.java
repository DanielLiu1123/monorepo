package monorepo.lib.common.context;

import io.micrometer.observation.ObservationRegistry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context information for request.
 *
 * @author Freeman
 * @since 2025/5/1
 */
public record Context(Map<String, List<String>> headers, ObservationRegistry observationRegistry) {
    public Context {
        headers = Map.copyOf(headers);
    }

    public Map<String, List<String>> getPropagatedHeaders() {
        var result = new HashMap<String, List<String>>();
        for (var entry : headers.entrySet()) {
            var name = entry.getKey();
            var values = entry.getValue();
            var matched = ContextConsts.propagatedHeaders.stream().anyMatch(p -> p.test(name, values));
            if (matched) {
                result.put(name, values);
            }
        }
        return result;
    }
}
