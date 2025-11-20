package monorepo.lib.common.context.webmv;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import monorepo.lib.common.context.Context;
import monorepo.lib.common.context.ContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author Freeman
 * @since 2025/11/19
 */
public final class ContextualOncePerRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContextHolder.set(buildContext(request));
        try {
            filterChain.doFilter(request, response);
        } finally {
            ContextHolder.remove();
        }
    }

    private static Context buildContext(HttpServletRequest request) {
        var headers = getHeaders(request);
        return new Context(headers);
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
}
