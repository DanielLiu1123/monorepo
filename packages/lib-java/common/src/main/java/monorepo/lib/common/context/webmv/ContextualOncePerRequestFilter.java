package monorepo.lib.common.context.webmv;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
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
        var ctx = new Context();

        var names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            var name = names.nextElement();
            ctx.addHeader(name, getValues(request, name));
        }

        return ctx;
    }

    private static ArrayList<String> getValues(HttpServletRequest request, String headerName) {
        var result = new ArrayList<String>();
        var values = request.getHeaders(headerName);
        while (values.hasMoreElements()) {
            result.add(values.nextElement());
        }
        return result;
    }
}
