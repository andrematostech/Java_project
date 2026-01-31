package org.estg.notifications.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String MEMBERS_EXCHANGE = "members-exchange";
    public static final String SCHEDULE_EXCHANGE = "schedule-exchange";
    public static final String WORKOUT_EXCHANGE = "workout.exchange";

    public static final String ROUTING_MEMBER_REGISTERED = "member.member_registered";

    public static final String ROUTING_SESSION_SCHEDULED = "schedule.session.scheduled";
    public static final String ROUTING_SESSION_RESCHEDULED = "schedule.session.rescheduled";
    public static final String ROUTING_SESSION_CANCELLED = "schedule.session.cancelled";

    public static final String ROUTING_WORKOUT_PLAN_CREATED = "workout.plan.created";
    public static final String ROUTING_WORKOUT_PLAN_UPDATED = "workout.plan.updated";
    public static final String ROUTING_WORKOUT_EXERCISE_COMPLETED = "workout.exercise.completed";

    public static final String MEMBER_REGISTERED_QUEUE = "notifications.member.registered.queue";
    public static final String SESSION_EVENTS_QUEUE = "notifications.session.queue";
    public static final String WORKOUT_EVENTS_QUEUE = "notifications.workout.queue";

    @Value("${rabbitmq.exchange.members:" + MEMBERS_EXCHANGE + "}")
    private String membersExchangeName;

    @Value("${rabbitmq.exchange.schedule:" + SCHEDULE_EXCHANGE + "}")
    private String scheduleExchangeName;

    @Value("${rabbitmq.exchange.workout:" + WORKOUT_EXCHANGE + "}")
    private String workoutExchangeName;

    @Bean
    public DirectExchange membersExchange() {
        return new DirectExchange(membersExchangeName, true, false);
    }

    @Bean
    public DirectExchange scheduleExchange() {
        return new DirectExchange(scheduleExchangeName, true, false);
    }

    @Bean
    public DirectExchange workoutExchange() {
        return new DirectExchange(workoutExchangeName, true, false);
    }

    @Bean(name = "memberRegisteredQueue")
    public Queue memberRegisteredQueue() {
        return new Queue(MEMBER_REGISTERED_QUEUE, true);
    }

    @Bean(name = "sessionEventsQueue")
    public Queue sessionEventsQueue() {
        return new Queue(SESSION_EVENTS_QUEUE, true);
    }

    @Bean(name = "workoutEventsQueue")
    public Queue workoutEventsQueue() {
        return new Queue(WORKOUT_EVENTS_QUEUE, true);
    }

    @Bean
    public Binding bindMemberRegistered(
            @Qualifier("memberRegisteredQueue") Queue q,
            DirectExchange membersExchange
    ) {
        return BindingBuilder.bind(q).to(membersExchange).with(ROUTING_MEMBER_REGISTERED);
    }

    @Bean
    public Binding bindSessionScheduled(
            @Qualifier("sessionEventsQueue") Queue q,
            DirectExchange scheduleExchange
    ) {
        return BindingBuilder.bind(q).to(scheduleExchange).with(ROUTING_SESSION_SCHEDULED);
    }

    @Bean
    public Binding bindSessionRescheduled(
            @Qualifier("sessionEventsQueue") Queue q,
            DirectExchange scheduleExchange
    ) {
        return BindingBuilder.bind(q).to(scheduleExchange).with(ROUTING_SESSION_RESCHEDULED);
    }

    @Bean
    public Binding bindSessionCancelled(
            @Qualifier("sessionEventsQueue") Queue q,
            DirectExchange scheduleExchange
    ) {
        return BindingBuilder.bind(q).to(scheduleExchange).with(ROUTING_SESSION_CANCELLED);
    }

    @Bean
    public Binding bindWorkoutPlanCreated(
            @Qualifier("workoutEventsQueue") Queue q,
            DirectExchange workoutExchange
    ) {
        return BindingBuilder.bind(q).to(workoutExchange).with(ROUTING_WORKOUT_PLAN_CREATED);
    }

    @Bean
    public Binding bindWorkoutPlanUpdated(
            @Qualifier("workoutEventsQueue") Queue q,
            DirectExchange workoutExchange
    ) {
        return BindingBuilder.bind(q).to(workoutExchange).with(ROUTING_WORKOUT_PLAN_UPDATED);
    }

    @Bean
    public Binding bindWorkoutExerciseCompleted(
            @Qualifier("workoutEventsQueue") Queue q,
            DirectExchange workoutExchange
    ) {
        return BindingBuilder.bind(q).to(workoutExchange).with(ROUTING_WORKOUT_EXERCISE_COMPLETED);
    }

    /**
     * IMPORTANT:
     * Use a simple converter so the consumer receives the raw AMQP Message (bytes).
     * This avoids cross-service coupling via "__TypeId__" headers from the producer.
     */
    @Bean
    public MessageConverter messageConverter() {
        return new SimpleMessageConverter();
    }
}
