package org.estg.trainers.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ROUTING_CREATED = "trainer.trainer_created";
    public static final String ROUTING_UPDATED = "trainer.trainer_updated";
    public static final String ROUTING_DELETED = "trainer.trainer_deleted";

    @Value("${rabbitmq.exchange.name:trainers-exchange}")
    private String trainersExchangeName;

    @Value("${rabbitmq.queue.trainer-created:trainer.created.queue}")
    private String trainerCreatedQueueName;

    @Value("${rabbitmq.queue.trainer-updated:trainer.updated.queue}")
    private String trainerUpdatedQueueName;

    @Value("${rabbitmq.queue.trainer-deleted:trainer.deleted.queue}")
    private String trainerDeletedQueueName;

    @Bean
    public DirectExchange trainersExchange() {
        return new DirectExchange(trainersExchangeName, true, false);
    }

    @Bean
    public Queue trainerCreatedQueue() {
        return new Queue(trainerCreatedQueueName, true);
    }

    @Bean
    public Queue trainerUpdatedQueue() {
        return new Queue(trainerUpdatedQueueName, true);
    }

    @Bean
    public Queue trainerDeletedQueue() {
        return new Queue(trainerDeletedQueueName, true);
    }

    @Bean
    public Binding trainerCreatedBinding(DirectExchange trainersExchange, Queue trainerCreatedQueue) {
        return BindingBuilder.bind(trainerCreatedQueue).to(trainersExchange).with(ROUTING_CREATED);
    }

    @Bean
    public Binding trainerUpdatedBinding(DirectExchange trainersExchange, Queue trainerUpdatedQueue) {
        return BindingBuilder.bind(trainerUpdatedQueue).to(trainersExchange).with(ROUTING_UPDATED);
    }

    @Bean
    public Binding trainerDeletedBinding(DirectExchange trainersExchange, Queue trainerDeletedQueue) {
        return BindingBuilder.bind(trainerDeletedQueue).to(trainersExchange).with(ROUTING_DELETED);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
