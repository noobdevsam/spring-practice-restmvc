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

    @Bean
    @Order(1)
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(
                        EndpointRequest.toAnyEndpoint()
                )
                .authorizeHttpRequests(
                        auth -> auth.anyRequest().permitAll()
                )
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(
                        auth -> {
                            //auth.requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll(); // for actuator health endpoint
                            auth
                                    .requestMatchers("/v3/api-docs**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                                    .anyRequest().authenticated();
                        }
                )
                .oauth2ResourceServer(
                        serverConfig -> serverConfig.jwt(Customizer.withDefaults())
                )
                .build();
    }
}

// Test the authorization with Postman for the following endpoint:
// GET http://localhost:8080/api/v1/beer
// Set http://localhost:9000/oauth2/token as OAuth2 token url when requesting the token
// and add OAuth data to request header when using Postman