package org.estg.schedule.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "org.estg.schedule.data")
public class JpaConfiguration {
}
