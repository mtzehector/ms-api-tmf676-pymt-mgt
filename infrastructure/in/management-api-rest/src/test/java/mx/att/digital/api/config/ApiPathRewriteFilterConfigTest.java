
package mx.att.digital.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class ApiPathRewriteFilterConfigTest {

    @Test @DisplayName("Bean registration y orden")
    void beanRegistration() {
        ApiPathRewriteFilterConfig cfg = new ApiPathRewriteFilterConfig();
        FilterRegistrationBean<?> bean = cfg.apiPathRewriteFilter();
        assertNotNull(bean);
        assertTrue(bean.getOrder() <= 0);
        assertFalse(bean.getUrlPatterns().isEmpty());
    }

    @Test @DisplayName("normalizeUri y resolveRewrittenPath cubren v5, v4->v5 y null")
    void helpersBranches() throws Exception {
        ApiPathRewriteFilterConfig cfg = new ApiPathRewriteFilterConfig();
        Method normalize = ApiPathRewriteFilterConfig.class.getDeclaredMethod("normalizeUri", String.class);
        Method resolve = ApiPathRewriteFilterConfig.class.getDeclaredMethod("resolveRewrittenPath", String.class);
        normalize.setAccessible(true);
        resolve.setAccessible(true);

        String u1 = (String) normalize.invoke(cfg, "/tmf-api/paymentManagement/v5/token");
        String r1 = (String) resolve.invoke(cfg, u1);
        assertTrue(r1 == null || r1.startsWith("/v5/"));

        String u2 = (String) normalize.invoke(cfg, "/v4/token");
        String r2 = (String) resolve.invoke(cfg, u2);
        assertEquals("/v5/token", r2);

        String u3 = (String) normalize.invoke(cfg, "/no-match/here");
        String r3 = (String) resolve.invoke(cfg, u3);
        assertNull(r3);
    }
}
