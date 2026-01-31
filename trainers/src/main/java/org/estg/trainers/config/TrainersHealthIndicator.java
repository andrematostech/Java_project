package org.estg.trainers.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import org.estg.trainers.data.TrainerRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom health indicator for Trainers service
 * Checks database connectivity and trainer count
 */
@Component
@Slf4j
public class TrainersHealthIndicator implements HealthIndicator {

    private final TrainerRepository trainerRepository;

    public TrainersHealthIndicator(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Override
    public Health health() {
        try {
            long trainerCount = trainerRepository.count();
            log.debug("Trainers health check - Count: {}", trainerCount);

            return Health.up()
                    .withDetail("trainers_count", trainerCount)
                    .withDetail("status", "Trainers service is operational")
                    .build();
        } catch (Exception e) {
            log.error("Trainers health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
