package org.estg.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Schedule Service - Spring Boot Application
 *
 * Microservice for managing training sessions
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ScheduleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScheduleApplication.class, args);
	}
}
