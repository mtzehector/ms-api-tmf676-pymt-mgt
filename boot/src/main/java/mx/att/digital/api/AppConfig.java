package mx.att.digital.api;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * The Class AppConfig.
 */
@Getter
@Configuration
public class AppConfig {

    /** The username. */
    @Value("${spring.security.user.name:default-user}")
    private String username;

    /** The password. */
    @Value("${spring.security.user.password:default-password}")
    private String password;

    /** The security enabled flag. */
    @Value("${app.security.enabled:false}")
    private boolean securityEnabled;

    /** The environment. */
    private final Environment environment;

    /**
     * Instantiates a new app config.
     *
     * @param environment the environment
     */
    public AppConfig(Environment environment) {
        this.environment = environment;
    }

    /**
     * Validate config.
     */
    @PostConstruct
    public void validateConfig() {
        if (securityEnabled && (username == null || password == null)) {
            throw new IllegalStateException("API credentials must be configured when security is enabled!");
        }
    }
}