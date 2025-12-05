package mx.att.digital.api.logging;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * The Class MDCFilterTest.
 */
class MDCFilterTest {

    /** The env. */
    private Environment env;
    
    /** The mdc filter. */
    private MDCFilter mdcFilter;
    
    /** The request. */
    private HttpServletRequest request;
    
    /** The response. */
    private HttpServletResponse response;
    
    /** The chain. */
    private FilterChain chain;

    /**
     * Sets the up.
     */
    @BeforeEach
    void setUp() {
        env = mock(Environment.class);
        mdcFilter = new MDCFilter(env);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
    }

    /**
     * Tear down.
     */
    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    /**
     * Configure request.
     *
     * @param traceId the trace id
     * @param clientId the client id
     * @param method the method
     * @param profile the profile
     * @param ipHeader the ip header
     * @param remoteAddr the remote addr
     * @param serverName the server name
     */
    private void configureRequest(String traceId, String clientId, String method, String profile,
                                  String ipHeader, String remoteAddr, String serverName) {
        when(request.getHeader("X-Request-Id")).thenReturn(traceId);
        when(request.getHeader("X-Request-Id-Client")).thenReturn(clientId);
        when(request.getMethod()).thenReturn(method);
        when(env.getProperty("spring.profiles.active")).thenReturn(profile);
        when(request.getHeader("X-Forwarded-For")).thenReturn(ipHeader);
        when(request.getRemoteAddr()).thenReturn(remoteAddr);
        when(request.getServerName()).thenReturn(serverName);
    }

    /**
     * Verify MDC.
     *
     * @param expectedTraceId the expected trace id
     * @param expectedRequestId the expected request id
     * @param expectedMethod the expected method
     * @param expectedEnv the expected env
     * @param expectedIp the expected ip
     * @param expectedHost the expected host
     */
    private void verifyMDC(String expectedTraceId, String expectedRequestId, String expectedMethod,
                           String expectedEnv, String expectedIp, String expectedHost) {
        try {
            doAnswer(invocation -> {
                assertEquals(expectedTraceId, MDC.get("traceId"));
                assertEquals(expectedRequestId, MDC.get("requestId"));
                assertEquals(expectedMethod, MDC.get("method"));
                assertEquals(expectedEnv, MDC.get("environment"));
                assertEquals(expectedIp, MDC.get("ipOrigin"));
                assertEquals(expectedHost, MDC.get("host"));
                return null;
            }).when(chain).doFilter(request, response);
        } catch (Exception e) {
            fail("Exception during MDC verification: " + e.getMessage());
        }
    }

    /**
     * Test do filter internal with headers sets MDC values.
     */
    @Test
    void testDoFilterInternal_withHeaders_setsMDCValues() {
        configureRequest("trace-123", "client-456", "POST", "PROD", "10.0.0.1", null, "localhost");
        verifyMDC("trace-123", "client-456", "POST", "PROD", "10.0.0.1", "localhost");

        try {
            mdcFilter.doFilterInternal(request, response, chain);
        } catch (Exception e) {
            fail("Exception during filter execution: " + e.getMessage());
        }

        assertTrue(MDC.getCopyOfContextMap() == null || MDC.getCopyOfContextMap().isEmpty());
    }

    /**
     * Test do filter internal missing headers generates defaults.
     */
    @Test
    void testDoFilterInternal_missingHeaders_generatesDefaults() {
        configureRequest(null, null, "GET", null, null, "::1", "myhost");

        try {
            doAnswer(invocation -> {
                String traceId = MDC.get("traceId");
                assertNotNull(traceId);
                assertEquals(traceId, MDC.get("requestId"));
                assertEquals("GET", MDC.get("method"));
                assertEquals("DEV", MDC.get("environment"));
                assertEquals("127.0.0.1", MDC.get("ipOrigin"));
                assertEquals("myhost", MDC.get("host"));
                return null;
            }).when(chain).doFilter(request, response);

            mdcFilter.doFilterInternal(request, response, chain);
        } catch (Exception e) {
            fail("Exception during filter execution: " + e.getMessage());
        }

        assertTrue(MDC.getCopyOfContextMap() == null || MDC.getCopyOfContextMap().isEmpty());
    }

    /**
     * Test do filter internal trace id empty generates UUID.
     */
    @Test
    void testDoFilterInternal_traceIdEmpty_generatesUUID() {
        configureRequest("", "client-456", "PUT", "QA", "192.168.1.10", null, "qa-server");

        try {
            doAnswer(invocation -> {
                String traceId = MDC.get("traceId");
                assertNotNull(traceId);
                assertNotEquals("", traceId);
                assertEquals("client-456", MDC.get("requestId"));
                assertEquals("PUT", MDC.get("method"));
                assertEquals("QA", MDC.get("environment"));
                assertEquals("192.168.1.10", MDC.get("ipOrigin"));
                assertEquals("qa-server", MDC.get("host"));
                return null;
            }).when(chain).doFilter(request, response);

            mdcFilter.doFilterInternal(request, response, chain);
        } catch (Exception e) {
            fail("Exception during filter execution: " + e.getMessage());
        }

        assertTrue(MDC.getCopyOfContextMap() == null || MDC.getCopyOfContextMap().isEmpty());
    }

    /**
     * Test do filter internal request id empty uses trace id as fallback.
     */
    @Test
    void testDoFilterInternal_requestIdEmpty_usesTraceIdAsFallback() {
        configureRequest("trace-789", "", "PATCH", "STAGE", "192.168.1.1", null, "stage-host");
        verifyMDC("trace-789", "trace-789", "PATCH", "STAGE", "192.168.1.1", "stage-host");

        try {
            mdcFilter.doFilterInternal(request, response, chain);
        } catch (Exception e) {
            fail("Exception during filter execution: " + e.getMessage());
        }

        assertTrue(MDC.getCopyOfContextMap() == null || MDC.getCopyOfContextMap().isEmpty());
    }

    /**
     * Test do filter internal ip is I pv 6 loopback is normalized to I pv 4.
     */
    @Test
    void testDoFilterInternal_ipIsIPv6Loopback_isNormalizedToIPv4() {
        configureRequest("trace-999", "client-999", "DELETE", "TEST", "0:0:0:0:0:0:0:1", null, "test-host");
        verifyMDC("trace-999", "client-999", "DELETE", "TEST", "127.0.0.1", "test-host");

        try {
            mdcFilter.doFilterInternal(request, response, chain);
        } catch (Exception e) {
            fail("Exception during filter execution: " + e.getMessage());
        }

        assertTrue(MDC.getCopyOfContextMap() == null || MDC.getCopyOfContextMap().isEmpty());
    }

    /**
     * Test do filter internal ip header empty uses remote addr.
     */
    @Test
    void testDoFilterInternal_ipHeaderEmpty_usesRemoteAddr() {
        configureRequest("trace-321", "client-654", "HEAD", "UAT", "", "192.168.0.100", "uat-host");
        verifyMDC("trace-321", "client-654", "HEAD", "UAT", "192.168.0.100", "uat-host");

        try {
            mdcFilter.doFilterInternal(request, response, chain);
        } catch (Exception e) {
            fail("Exception during filter execution: " + e.getMessage());
        }

        assertTrue(MDC.getCopyOfContextMap() == null || MDC.getCopyOfContextMap().isEmpty());
    }
}