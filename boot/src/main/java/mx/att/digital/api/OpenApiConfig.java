package mx.att.digital.api;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Class OpenApiConfig.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Agreement api.
     *
     * @return the grouped open api
     */
    @Bean
    public GroupedOpenApi agreementApi() {
        return GroupedOpenApi.builder()
                .group("agreement-management")
                .packagesToScan("mx.att.digital.api.adapters")
                .build();
    }
}
