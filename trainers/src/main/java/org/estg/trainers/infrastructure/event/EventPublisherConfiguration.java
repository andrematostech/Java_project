package org.estg.trainers.infrastructure.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Chooses the event publisher implementation by profile.
 */
@Configuration
public class EventPublisherConfiguration {

    @Bean
    @Profile("!docker & !k8s")
    public EventPublisher inMemoryEventPublisher() {
        return new InMemoryEventPublisher();
    }

    @Bean
    @Profile({"docker", "k8s"})
    public EventPublisher rabbitMQEventPublisher(RabbitTemplate rabbitTemplate) {
        // exchangeName is injected inside RabbitMQEventPublisher via @Value
        return new RabbitMQEventPublisher(rabbitTemplate, "trainers-exchange");
    }
}
