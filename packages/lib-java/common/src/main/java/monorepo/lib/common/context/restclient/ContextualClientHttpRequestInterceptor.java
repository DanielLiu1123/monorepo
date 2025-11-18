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
        var context = ContextHolder.get();
        if (context == null) {
            return execution.execute(request, body);
        }

        for (var en : context.getHeaders().entrySet()) {
            var key = en.getKey();
            var values = en.getValue();
            request.getHeaders().addAll(key, values);
        }
        return execution.execute(request, body);
    }
}
