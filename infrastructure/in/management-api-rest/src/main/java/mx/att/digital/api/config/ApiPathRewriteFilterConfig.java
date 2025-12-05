package mx.att.digital.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Configuration
public class ApiPathRewriteFilterConfig {

  private static final Logger log = LoggerFactory.getLogger(ApiPathRewriteFilterConfig.class);

  // Prefijo lógico de la TMF676
  private static final String MGMT_PREFIX = "/paymentManagement";

  // Prefijo típico del microservicio en preprod / WSO2
  private static final String MS_PREFIX = "/ms-api-tmf676-pymt-mgt";

  @Bean
  public FilterRegistrationBean<OncePerRequestFilter> apiPathRewriteFilter() {
    FilterRegistrationBean<OncePerRequestFilter> reg = new FilterRegistrationBean<>();
    reg.setFilter(new OncePerRequestFilter() {
      @Override
      protected void doFilterInternal(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain chain
      ) throws ServletException, IOException {

        final String originalUri = request.getRequestURI();
        if (originalUri == null) {
          chain.doFilter(request, response);
          return;
        }

        // Log de trazabilidad
        log.info("[TMF676] Incoming URI: {}", originalUri);

        // 1) Normalizamos quitando el prefijo del micro si viene presente:
        //    /ms-api-tmf676-pymt-mgt/v4/token  ->  /v4/token
        //    /ms-api-tmf676-pymt-mgt/paymentManagement/v5/token -> /paymentManagement/v5/token
        String uri = originalUri;
        if (uri.startsWith(MS_PREFIX)) {
          uri = uri.substring(MS_PREFIX.length());
          if (uri.isEmpty()) {
            uri = "/";
          }
        }

        log.info("[TMF676] Normalized URI (without MS_PREFIX): {}", uri);

        // 2) Si YA viene como /paymentManagement/v5/... lo dejamos pasar tal cual
        if (uri.startsWith(MGMT_PREFIX + "/v5/")) {
          chain.doFilter(request, response);
          return;
        }

        // 3) Buscamos /v5/ o /v4/ en cualquier parte de la URI normalizada
        int idx = uri.indexOf("/v5/");
        String normalized;
        if (idx >= 0) {
          // Ej: /algo/v5/token -> /v5/token
          normalized = uri.substring(idx);
        } else {
          idx = uri.indexOf("/v4/");
          if (idx >= 0) {
            // Ej: /v4/token  -> /v5/token
            normalized = "/v5/" + uri.substring(idx + 4);
          } else {
            normalized = null;
          }
        }

        // 4) Si logramos normalizar, hacemos forward a /paymentManagement/v5/...
        if (normalized != null) {
          final String target = MGMT_PREFIX + normalized;  // /paymentManagement/v5/token
          log.info("[TMF676] Rewriting URI '{}' -> '{}'", originalUri, target);
          request.getRequestDispatcher(target).forward(request, response);
          return;
        }

        // 5) Si no aplica ninguna regla, dejamos pasar
        chain.doFilter(request, response);
      }
    });
    reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
    reg.setUrlPatterns(Collections.singletonList("/*"));
    return reg;
  }
}
