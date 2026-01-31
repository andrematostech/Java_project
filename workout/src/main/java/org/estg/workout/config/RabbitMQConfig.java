package org.estg.workout.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange used by Workout service
    public static final String WORKOUT_EXCHANGE = "workout.exchange";

    // Routing keys published by Workout service
    public static final String ROUTING_CREATED = "workout.plan.created";
    public static final String ROUTING_UPDATED = "workout.plan.updated";
    public static final String ROUTING_EXERCISE_COMPLETED = "workout.exercise.completed";

    // Default queue (Notifications consumes this one)
    public static final String WORKOUT_QUEUE = "notifications.workout.queue";

    @Bean
    public DirectExchange workoutExchange() {
        return new DirectExchange(WORKOUT_EXCHANGE, true, false);
    }

    @Bean
    public Queue workoutQueue() {
        return new Queue(WORKOUT_QUEUE, true);
    }

    @Bean
    public Binding bindWorkoutCreated(Queue workoutQueue, DirectExchange workoutExchange) {
        return BindingBuilder.bind(workoutQueue).to(workoutExchange).with(ROUTING_CREATED);
    }

    @Bean
    public Binding bindWorkoutUpdated(Queue workoutQueue, DirectExchange workoutExchange) {
        return BindingBuilder.bind(workoutQueue).to(workoutExchange).with(ROUTING_UPDATED);
    }

    @Bean
    public Binding bindWorkoutExerciseCompleted(Queue workoutQueue, DirectExchange workoutExchange) {
        return BindingBuilder.bind(workoutQueue).to(workoutExchange).with(ROUTING_EXERCISE_COMPLETED);
    }

    // Ensure messages are serialized as JSON
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
