package org.estg.workout.infrastructure.event;

import org.estg.workout.domain.event.WorkoutEventPublisher;
import org.estg.workout.domain.event.WorkoutPlanEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * No-op publisher used when you do not want to emit events.
 */
@Component
@ConditionalOnProperty(name = "app.events.publisher", havingValue = "noop", matchIfMissing = true)
public class NoOpWorkoutEventPublisher implements WorkoutEventPublisher {

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
}
