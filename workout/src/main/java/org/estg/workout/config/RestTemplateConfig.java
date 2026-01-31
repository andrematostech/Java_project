package org.estg.workout.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate used by infrastructure clients (MembersClient/TrainersClient).
 *
 * Important:
 * - Previously this bean only existed in the "docker" profile.
 * - In Kubernetes we run with "k8s" profile, so the bean was missing and the app crashed.
 */
@Configuration
@Profile({ "docker", "k8s" }) // enable for both Docker Compose and Kubernetes
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(
            RestTemplateBuilder builder,
            @Value("${gymhub.security.basic.username:admin}") String user,
            @Value("${gymhub.security.basic.password:admin}") String pass) {

        // Keep basic auth behavior consistent; can be overridden via properties/env if needed.
        return builder
                .basicAuthentication(user, pass)
                .connectTimeout(Duration.ofSeconds(3))
                .readTimeout(Duration.ofSeconds(5))
                .build();
    }
}
