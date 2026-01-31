package org.estg.workout.config;

import org.estg.workout.domain.event.WorkoutEventPublisher;
import org.estg.workout.domain.event.WorkoutPlanEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

/**
 * Test-only bean definitions so the Spring context can start during tests.
 */
@TestConfiguration
public class TestWorkoutEventPublisherConfig {

    @Bean
    @Primary
    public WorkoutEventPublisher workoutEventPublisher() {
        return new WorkoutEventPublisher() {

            @Override
            public void publishPlanCreated(WorkoutPlanEvent event) {
                // no-op
            }

            @Override
            public void publishPlanUpdated(WorkoutPlanEvent event) {
                // no-op
            }

            @Override
            public void publishExerciseCompleted(WorkoutPlanEvent event) {
                // no-op
            }
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
