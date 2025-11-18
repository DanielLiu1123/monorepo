package monorepo.lib.common.context;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Freeman
 * @since 2025/5/1
 */
public final class Context {

    private final Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public void addHeader(String key, List<String> value) {
        headers.put(key, value);
    }

    public void addHeaders(Map<String, List<String>> headers) {
        this.headers.putAll(headers);
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }
}
