package monorepo.lib.common.context.restclient;

import java.io.IOException;
import monorepo.lib.common.context.ContextHolder;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 *
 *
 * @author Freeman
 * @since 2025/11/19
 */
public final class ContextualClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        var context = ContextHolder.getOrNull();
        if (context == null) {
            return execution.execute(request, body);
        }

        for (var en : context.getPropagatedHeaders().entrySet()) {
            var name = en.getKey();
            var headers = request.getHeaders();
            if (!headers.containsHeader(name)) {
                headers.put(name, en.getValue());
            }
        }
        return execution.execute(request, body);
    }
}
