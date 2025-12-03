package monorepo.lib.common.pagination;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import monorepo.lib.common.util.JsonUtil;

/**
 *
 *
 * @author Freeman
 * @since 2025/12/4
 */
public record PageTokenState(String lastId, long offset, String filterHash, String sortHash) {

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
