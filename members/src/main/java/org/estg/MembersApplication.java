package org.estg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Members microservice main application
 * Feign is NOT enabled because this service does not call other services
 */
@SpringBootApplication
public class MembersApplication {

    public static void main(String[] args) {
        SpringApplication.run(MembersApplication.class, args);
    }
}
