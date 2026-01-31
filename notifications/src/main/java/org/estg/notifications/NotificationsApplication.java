package org.estg.notifications;

import org.springframework.boot.SpringApplication; // boot runner
import org.springframework.boot.autoconfigure.SpringBootApplication; // boot app

/**
 * Main entry point for Notifications service.
 * This service will later consume RabbitMQ events.
 */
@SpringBootApplication // enables Spring Boot auto config
public class NotificationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationsApplication.class, args); // start service
    }
}
