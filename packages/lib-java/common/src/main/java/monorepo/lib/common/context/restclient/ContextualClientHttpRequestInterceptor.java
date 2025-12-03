package monorepo.lib.common.context.restclient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import monorepo.lib.common.context.ContextHolder;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
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

        // Record request body to observation
        var observation = context.observationRegistry().getCurrentObservation();
        if (observation != null && !observation.isNoop()) {
            var requestBody = extractRequestBody(request, body);
            observation.highCardinalityKeyValue("http.request.body", requestBody);
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

    private static String extractRequestBody(HttpRequest request, byte[] body) {
        var contentType = request.getHeaders().getContentType();
        if (contentType != null && MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            if (body.length > 0) {
                return new String(body, StandardCharsets.UTF_8);
            }
            return "";
        }
        return "(request body is not JSON)";
    }
}
