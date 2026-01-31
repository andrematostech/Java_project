package org.estg.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GymHub API")
                        .version("1.0.0")
                        .description("GymHub Microservices API Gateway")
                        .contact(new Contact()
                                .name("GymHub Team")
                                .email("gymhub@example.com")));
    }
}
