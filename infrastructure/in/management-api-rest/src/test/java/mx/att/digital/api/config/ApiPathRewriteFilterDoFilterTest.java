
package mx.att.digital.api.config;

import jakarta.servlet.Filter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiPathRewriteFilterDoFilterTest {

    @ParameterizedTest(name = "{1}")
    @CsvSource({
        "/v4/token, doFilter() procesa una URL con /v4/ sin lanzar excepción",
        "/tmf-api/paymentManagement/v5/token, doFilter() con ruta tmf-api v5 pasa sin error",
        "/health, doFilter() con ruta sin rewrite (health) pasa sin error"
    })
    void doFilter(String path, String description) throws Exception {
        ApiPathRewriteFilterConfig cfg = new ApiPathRewriteFilterConfig();
        FilterRegistrationBean<?> bean = cfg.apiPathRewriteFilter();
        Filter filter = bean.getFilter();

        MockHttpServletRequest req = new MockHttpServletRequest("GET", path);
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(req, res, chain);
        assertEquals(200, res.getStatus());
    }
}
