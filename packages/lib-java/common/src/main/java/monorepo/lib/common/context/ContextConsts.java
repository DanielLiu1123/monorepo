package monorepo.lib.common.context;

import java.util.Set;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/20
 */
public final class ContextConsts {
    private ContextConsts() {}

    public static final Set<String> blockedHeaders = Set.of(
            // Hop-by-hop headers
            "connection",
            "keep-alive",
            "transfer-encoding",
            "upgrade",
            "proxy-connection",
            "proxy-authenticate",
            "proxy-authorization",
            "te",
            "trailer",
            // sensitive headers
            "cookie",
            "set-cookie",
            // fundamental headers
            "host",
            "content-length");
}
