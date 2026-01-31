package org.estg.workout.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Central RestClient configuration for calling other GymHub services.
 * In Docker, internal calls must include Basic Auth because services protect endpoints.
 */
@Configuration
public class RestClientConfig {

    @Value("${gymhub.security.basic.username:}")
    private String basicUser;

    @Value("${gymhub.security.basic.password:}")
    private String basicPass;

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        // If credentials are configured, attach Authorization header to every request.
        if (basicUser != null && !basicUser.isBlank()) {
            String token = Base64.getEncoder().encodeToString(
                    (basicUser + ":" + basicPass).getBytes(StandardCharsets.UTF_8)
            );

            return builder
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + token)
                    .build();
        }

        // Otherwise, plain client (useful for local dev if security disabled).
        return builder.build();
    }
}
