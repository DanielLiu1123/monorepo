package monorepo.lib.common.pagination;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import monorepo.lib.common.util.JsonUtil;

/**
 * Page token state for cursor-based pagination.
 * Stores the last record's sorting field values to enable accurate pagination with custom sort orders.
 *
 * @author Freeman
 * @since 2025/12/4
 */
public record PageTokenState(Map<String, String> lastValues, String filterHash, String sortHash) {

    public String toPageToken() {
        var json = JsonUtil.stringify(this);
        var combined = Base64.getUrlEncoder().withoutPadding().encode(json.getBytes(StandardCharsets.UTF_8));
        return new String(combined, StandardCharsets.UTF_8);
    }

    public static PageTokenState fromPageToken(String pageToken) {
        var combined = Base64.getUrlDecoder().decode(pageToken);
        var json = new String(combined, StandardCharsets.UTF_8);
        return JsonUtil.parse(json, PageTokenState.class);
    }
}
