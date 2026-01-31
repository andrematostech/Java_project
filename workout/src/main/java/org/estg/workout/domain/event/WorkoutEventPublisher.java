package org.estg.workout.domain.event;

/**
 * Domain interface to publish workout-related events.
 */
public interface WorkoutEventPublisher {

    // Publish event when a workout plan is created
    void publishPlanCreated(WorkoutPlanEvent event);

    // Publish event when a workout plan is updated
    void publishPlanUpdated(WorkoutPlanEvent event);

    // Publish event when an exercise is completed
    void publishExerciseCompleted(WorkoutPlanEvent event);
}
