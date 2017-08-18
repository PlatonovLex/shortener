package ru.platonov.shortener.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration.
 * <p>
 *     Site configuration
 * </p>
 *
 * @author Platonov Alexey
 * @since 16.08.2017
 */
@Configuration
@ConfigurationProperties(prefix = "domain")
@Getter
@Setter
public class DomainConfig {

    /**
     * Domain name: localhost is default
     */
    private String name;

    /**
     * Specified port in application.properties,
     * or actual embedded server port by default
     */
    private int port;

}
