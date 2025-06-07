package com.example.springpracticerestmvc.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("!test") // Exclude this configuration in test profile
public class SecConfig {

    /**
     * Configures a security filter chain specifically for Spring Boot Actuator endpoints.
     * <p>
     * This filter chain matches any Actuator endpoint using `EndpointRequest.toAnyEndpoint()`
     * and permits all requests to these endpoints without requiring authentication.
     * <p>
     * This configuration is useful for allowing unrestricted access to Actuator endpoints,
     * which are typically used for monitoring and management purposes.
     *
     * @param http the HttpSecurity object used to configure security settings
     * @return a SecurityFilterChain that permits all requests to Actuator endpoints
     * @throws Exception if an error occurs while building the security configuration
     */
    @Bean
    @Order(1)
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(
                        EndpointRequest.toAnyEndpoint() // Matches any Actuator endpoint
                )
                .authorizeHttpRequests(
                        auth -> auth.anyRequest().permitAll() // Permits all requests to matched endpoints
                )
                .build();
    }

    /**
     * Configures the main security filter chain for the application.
     * <p>
     * This filter chain:
     * - Permits unauthenticated access to API documentation and Swagger UI endpoints.
     * - Requires authentication for all other requests.
     * - Configures the application to use JWT-based authentication for OAuth2 resource server.
     *
     * @param http the HttpSecurity object used to configure security settings
     * @return a SecurityFilterChain that enforces the specified security rules
     * @throws Exception if an error occurs while building the security configuration
     */
    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(
                        auth -> {
                            auth
                                    .requestMatchers("/v3/api-docs**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() // Allow public access to API docs and Swagger UI
                                    .anyRequest().authenticated(); // Require authentication for all other requests
                        }
                )
                .oauth2ResourceServer(
                        serverConfig -> serverConfig.jwt(Customizer.withDefaults()) // Enable JWT-based authentication for OAuth2 resource server
                )
                .build();
    }
}

// Test the authorization with Postman for the following endpoint:
// GET http://localhost:8080/api/v1/beer
// Set http://localhost:9000/oauth2/token as OAuth2 token url when requesting the token
// and add OAuth data to request header when using Postman