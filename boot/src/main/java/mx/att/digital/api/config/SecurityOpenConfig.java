package mx.att.digital.api.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Open security config used when app.security.enabled=false (default).
 * This ensures endpoints are publicly accessible and prevents Spring Boot's default
 * Basic authentication auto-configuration from kicking in.
 */
@Configuration
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "false", matchIfMissing = true)
public class SecurityOpenConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Security Hotspot Justification:
            // - This configuration is used for development/testing environments only (app.security.enabled=false)
            // - All endpoints are publicly accessible without authentication
            // - This is a stateless REST API without session management
            // - No cookies or session state are used (stateless)
            // - CSRF protection is not applicable when there's no authentication
            // - In production, SecurityConfig with proper authentication should be used instead
            // - This configuration should NEVER be enabled in production environments
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        
        return http.build();
    }
}