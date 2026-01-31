package org.estg.workout;

import org.estg.workout.config.TestWorkoutEventPublisherConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Loads the Spring context for the workout service using the "test" profile.
 * We import a small test configuration that provides missing beans required
 * by the application context (publisher + RestTemplate).
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestWorkoutEventPublisherConfig.class)
class WorkoutApplicationTests {

    @Test
    void contextLoads() {
        // Context load test: passes if the Spring application context starts successfully.
    }
}
