package com.example.springpracticerestmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(
                        auth -> auth.anyRequest().authenticated()
                )
                .csrf(
                        csrf -> csrf.ignoringRequestMatchers("/api/**")
                )
                .httpBasic(
                        Customizer.withDefaults()
                )
                .build();
    }
}
