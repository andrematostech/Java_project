package org.estg.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.name:members-exchange}")
    private String exchangeName;

    @Value("${rabbitmq.queue.member-registered:member.registered.queue}")
    private String memberRegisteredQueue;

    @Value("${rabbitmq.queue.member-activated:member.activated.queue}")
    private String memberActivatedQueue;

    @Value("${rabbitmq.queue.member-profile-updated:member.profile.updated.queue}")
    private String memberProfileUpdatedQueue;

    @Value("${rabbitmq.queue.member-suspended:member.suspended.queue}")
    private String memberSuspendedQueue;

    // Exchange
    @Bean
    public DirectExchange membersExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    // JSON converter for message payloads
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate configured with the JSON message converter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    // Queues
    @Bean
    public Queue memberRegisteredQueue() {
        return new Queue(memberRegisteredQueue, true);
    }

    @Bean
    public Queue memberActivatedQueue() {
        return new Queue(memberActivatedQueue, true);
    }

    @Bean
    public Queue memberProfileUpdatedQueue() {
        return new Queue(memberProfileUpdatedQueue, true);
    }

    @Bean
    public Queue memberSuspendedQueue() {
        return new Queue(memberSuspendedQueue, true);
    }

    // Bindings (use @Qualifier to avoid ambiguous Queue injection)
    @Bean
    public Binding bindMemberRegistered(
            @Qualifier("memberRegisteredQueue") Queue q,
            DirectExchange membersExchange
    ) {
        return BindingBuilder.bind(q)
                .to(membersExchange)
                .with("member.member_registered");
    }

    @Bean
    public Binding bindMemberActivated(
            @Qualifier("memberActivatedQueue") Queue q,
            DirectExchange membersExchange
    ) {
        return BindingBuilder.bind(q)
                .to(membersExchange)
                .with("member.member_activated");
    }

    @Bean
    public Binding bindMemberProfileUpdated(
            @Qualifier("memberProfileUpdatedQueue") Queue q,
            DirectExchange membersExchange
    ) {
        return BindingBuilder.bind(q)
                .to(membersExchange)
                .with("member.member_profile_updated");
    }

    @Bean
    public Binding bindMemberSuspended(
            @Qualifier("memberSuspendedQueue") Queue q,
            DirectExchange membersExchange
    ) {
        return BindingBuilder.bind(q)
                .to(membersExchange)
                .with("member.member_suspended");
    }
}
