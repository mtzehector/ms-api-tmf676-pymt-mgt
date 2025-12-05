package mx.att.digital.api.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * The Class MDCFilter.
 */
@Component
public class MDCFilter extends OncePerRequestFilter {

    /** The env. */
    private final Environment env;

    /**
     * Instantiates a new MDC filter.
     *
     * @param env the env
     */
    public MDCFilter(Environment env) {
        this.env = env;
    }

    /**
     * Do filter internal.
     *
     * @param request the request
     * @param response the response
     * @param chain the chain
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        // Trace ID 
        String traceId = request.getHeader("X-Request-Id");
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }
        // Request ID
        String requestId = request.getHeader("X-Request-Id-Client");
        if (requestId == null || requestId.isEmpty()) {
            requestId = traceId;
        }

        // Método HTTP
        String method = request.getMethod();

        // Entorno desde perfiles activos (o fallback)
        String environment = env.getProperty("spring.profiles.active");
        if (environment == null) {
            environment = "DEV";
        }

        // Inyecta en MDC antes de la ejecución
        MDC.put("traceId", traceId);
        MDC.put("requestId", requestId);
        MDC.put("method", method);
        MDC.put("environment", environment);
        
        // ✅ Campos adicionales:
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "127.0.0.1";
        }
        MDC.put("ipOrigin", ip);
        MDC.put("host", request.getServerName());

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
