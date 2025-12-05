package mx.att.digital.api.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

/**
 * RestTemplate SSL Config para TMF676 (Payment connector).
 */
@Configuration
public class RestTemplateConfig676 {

    @Value("${tmf676.ssl.trust-store}")
    private Resource trustStore;

    @Value("${tmf676.ssl.trust-store-password}")
    private String trustStorePassword;

    @Bean(name = "restTemplate676")
    public RestTemplate restTemplate676() {
        try {

            SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(
                    trustStore.getURL(),
                    trustStorePassword.toCharArray()
                )
                .build();

            PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setTlsSocketStrategy(new DefaultClientTlsStrategy(sslContext))
                    .build();
            
            connectionManager.setMaxTotal(100);
            connectionManager.setDefaultMaxPerRoute(10);

            HttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

            HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

            return new RestTemplate(requestFactory);

        } catch (Exception ex) {
            throw new IllegalStateException("Error configurando SSL para TMF676", ex);
        }
    }
}
