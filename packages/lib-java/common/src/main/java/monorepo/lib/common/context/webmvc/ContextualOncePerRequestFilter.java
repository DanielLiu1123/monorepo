package monorepo.lib.common.context.webmvc;

import io.micrometer.observation.ObservationRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import monorepo.lib.common.context.Context;
import monorepo.lib.common.context.ContextHolder;
import monorepo.lib.common.util.JsonUtil;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * @author Freeman
 * @since 2025/11/19
 */
public final class ContextualOncePerRequestFilter extends OncePerRequestFilter {

    private final ObservationRegistry observationRegistry;

    public ContextualOncePerRequestFilter(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Wrap request to cache the body for reading
        var wrappedRequest = new ContentCachingRequestWrapper(request, 0);

        var context = buildContext(wrappedRequest);
        ContextHolder.runWithContext(context, () -> {
            try {
                filterChain.doFilter(wrappedRequest, response);

                // Read and record request body after the filter chain completes
                var observation = observationRegistry.getCurrentObservation();
                if (observation != null && !observation.isNoop()) {
                    observation.highCardinalityKeyValue("http.request.body", extractRequestBody(wrappedRequest));
                    observation.highCardinalityKeyValue("http.request.headers", JsonUtil.stringify(context.headers()));
                }
            } catch (Throwable t) {
                sneakyThrow(t);
            }
        });
    }

    private static String extractRequestBody(ContentCachingRequestWrapper request) {
        try {
            var mediaType = MediaType.parseMediaType(request.getContentType());
            if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) {
                var content = request.getContentAsByteArray();
                if (content.length > 0) {
                    return new String(content, StandardCharsets.UTF_8);
                }
            }
            return "";
        } catch (Exception e) {
            return "(failed to extract request body: " + e.getMessage() + ")";
        }
    }

    private Context buildContext(HttpServletRequest request) {
        var headers = getHeaders(request);
        return new Context(headers, observationRegistry);
    }

    private static Map<String, List<String>> getHeaders(HttpServletRequest request) {
        var result = new HashMap<String, List<String>>();
        var names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            var name = names.nextElement();
            result.put(name, getValues(request, name));
        }
        return result;
    }

    private static List<String> getValues(HttpServletRequest request, String headerName) {
        var result = new ArrayList<String>();
        var values = request.getHeaders(headerName);
        while (values.hasMoreElements()) {
            result.add(values.nextElement());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow(Throwable t) throws T {
        throw (T) t;
    }
}
