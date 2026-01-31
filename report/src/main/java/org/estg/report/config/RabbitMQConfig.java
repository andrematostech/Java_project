package org.estg.report.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Report-service consumes events produced by other services.
 *
 * IMPORTANT:
 * - We declare our own queues so multiple consumers can exist without sharing a queue.
 * - We bind our queues to the producer exchanges (members/schedule/workout) using routing keys.
 * - @Qualifier is used to avoid ambiguity when multiple beans of the same type exist.
 */
@Configuration
public class RabbitMQConfig {

    // Producer exchange names (must match the producing services)
    public static final String MEMBERS_EXCHANGE = "members-exchange";   // from members-service
    public static final String SCHEDULE_EXCHANGE = "schedule-exchange"; // from schedule-service
    public static final String WORKOUT_EXCHANGE = "workout.exchange";   // from workout-service

    // Routing keys (must match the producing services)
    public static final String ROUTING_MEMBER_REGISTERED = "member.member_registered";
    public static final String ROUTING_SESSION_COMPLETED = "schedule.session.completed"; // FIXED
    public static final String ROUTING_WORKOUT_PLAN_CREATED = "workout.plan.created";

    // Report-service owned queues
    public static final String MEMBER_REGISTERED_QUEUE = "report.member.registered.queue";
    public static final String SESSION_COMPLETED_QUEUE = "report.session.completed.queue";
    public static final String WORKOUT_PLAN_CREATED_QUEUE = "report.workout.plan.created.queue";

    // Allow overriding via properties (defaults remain correct)
    @Value("${rabbitmq.exchange.members:" + MEMBERS_EXCHANGE + "}")
    private String membersExchangeName;

    @Value("${rabbitmq.exchange.schedule:" + SCHEDULE_EXCHANGE + "}")
    private String scheduleExchangeName;

    @Value("${rabbitmq.exchange.workout:" + WORKOUT_EXCHANGE + "}")
    private String workoutExchangeName;

    // Exchanges (durable direct exchanges)
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

    // Queues (durable)
    @Bean(name = "memberRegisteredQueue")
    public Queue memberRegisteredQueue() {
        return new Queue(MEMBER_REGISTERED_QUEUE, true);
    }

    @Bean(name = "sessionCompletedQueue")
    public Queue sessionCompletedQueue() {
        return new Queue(SESSION_COMPLETED_QUEUE, true);
    }

    @Bean(name = "workoutPlanCreatedQueue")
    public Queue workoutPlanCreatedQueue() {
        return new Queue(WORKOUT_PLAN_CREATED_QUEUE, true);
    }

    // Bindings
    @Bean
    public Binding bindMemberRegistered(
            @Qualifier("memberRegisteredQueue") Queue q,
            DirectExchange membersExchange
    ) {
        return BindingBuilder.bind(q).to(membersExchange).with(ROUTING_MEMBER_REGISTERED);
    }

    @Bean
    public Binding bindSessionCompleted(
            @Qualifier("sessionCompletedQueue") Queue q,
            DirectExchange scheduleExchange
    ) {
        return BindingBuilder.bind(q).to(scheduleExchange).with(ROUTING_SESSION_COMPLETED);
    }

    @Bean
    public Binding bindWorkoutPlanCreated(
            @Qualifier("workoutPlanCreatedQueue") Queue q,
            DirectExchange workoutExchange
    ) {
        return BindingBuilder.bind(q).to(workoutExchange).with(ROUTING_WORKOUT_PLAN_CREATED);
    }

    // JSON converter for @RabbitListener payload binding
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
