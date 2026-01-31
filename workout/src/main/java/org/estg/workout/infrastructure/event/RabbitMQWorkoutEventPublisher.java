package org.estg.workout.infrastructure.event;

import org.estg.workout.domain.event.WorkoutEventPublisher;
import org.estg.workout.domain.event.WorkoutPlanEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ publisher for workout-related events.
 */
@Component
@ConditionalOnProperty(name = "app.events.publisher", havingValue = "rabbit")
public class RabbitMQWorkoutEventPublisher implements WorkoutEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;

    public RabbitMQWorkoutEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.exchange:workout.exchange}") String exchangeName
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
    }

    @Override
    public void publishPlanCreated(WorkoutPlanEvent event) {
        rabbitTemplate.convertAndSend(exchangeName, "workout.plan.created", event);
    }

    @Override
    public void publishPlanUpdated(WorkoutPlanEvent event) {
        rabbitTemplate.convertAndSend(exchangeName, "workout.plan.updated", event);
    }

    @Override
    public void publishExerciseCompleted(WorkoutPlanEvent event) {
        rabbitTemplate.convertAndSend(exchangeName, "workout.exercise.completed", event);
    }
}
