package com.example.springpracticerestmvc.controllers;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.atlassian.oai.validator.whitelist.ValidationErrorsWhitelist;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import static com.atlassian.oai.validator.whitelist.rule.WhitelistRules.messageHasKey;

/**
 * Integration tests for the BeerController using RestAssured and OpenAPI validation.
 * This class tests the BeerController endpoints against the OpenAPI specification.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(BeerControllerRestAssuredTest.TestConfig.class)
@ComponentScan(basePackages = "com.example.springpracticerestmvc")
public class BeerControllerRestAssuredTest {

    /**
     * The port on which the local server is running during tests.
     */
    @LocalServerPort
    Integer localPort;

    /**
     * OpenAPI validation filter for validating API responses against the OpenAPI specification.
     */
    OpenApiValidationFilter filter = new OpenApiValidationFilter(
            OpenApiInteractionValidator
                    .createForSpecificationUrl("openapi3.yml")
                    .withWhitelist(
                            ValidationErrorsWhitelist
                                    .create()
                                    .withRule(
                                            "Ignore date format",
                                            messageHasKey("validation.response.body.schema.format.date-time")
                                    )
                    )
                    .build()
    );

    /**
     * Sets up the RestAssured base URI and port before each test.
     */
    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = localPort;
    }

    /**
     * Tests the BeerController's endpoint for listing beers.
     * Verifies that the response status code is 200 (OK).
     */
    @Test
    void test_list_beers() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .filter(filter)
                .get("/api/v1/beer")
                .then()
                .assertThat().statusCode(200);
    }

    /**
     * Configuration class for test-specific security settings.
     */
    @Configuration
    public static class TestConfig {

        /**
         * Configures a security filter chain for tests, allowing all requests.
         *
         * @param http the HttpSecurity object used to configure security settings
         * @return the configured SecurityFilterChain
         * @throws Exception if an error occurs during configuration
         */
        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .authorizeHttpRequests(
                            auth -> auth.anyRequest().permitAll()
                    )
                    .build();
        }
    }

}