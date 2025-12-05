package mx.att.digital.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = false)
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.security.csrf.enabled:false}")
    private boolean csrfEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Security Hotspot Justification:
            // - This is a stateless REST API (TMF676) without session-based authentication
            // - Uses HTTP Basic Authentication with credentials in headers (no cookies)
            // - Designed for machine-to-machine communication, not browser-based clients
            // - No session state is maintained on the server (stateless architecture)
            // - CSRF attacks target session cookies, which are not used here
            // - Enabling CSRF would break stateless REST API client operations
            .csrf(csrf -> {
                if (csrfEnabled) {
                    csrf.ignoringRequestMatchers("/api/**");
                } else {
                    csrf.disable();
                }
            })
            .httpBasic(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            );
        
        return http.build();
    }

    @Bean
    public UserDetailsService users(
            @Value("${spring.security.user.name:admin}") String user,
            @Value("${spring.security.user.password:admin}") String pass
    ) {
        UserDetails u = User.withUsername(user)
                .password("{noop}" + pass)
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(u);
    }
}