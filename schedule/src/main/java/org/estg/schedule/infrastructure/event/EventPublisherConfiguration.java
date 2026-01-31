package org.estg.schedule.infrastructure.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
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
    public EventPublisher inMemoryEventPublisher(ApplicationEventPublisher appPublisher) {
        return new InMemoryEventPublisher(appPublisher);
    }

    @Bean
    @Profile({"docker", "k8s"})
    public EventPublisher rabbitMQEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange.name:schedule-exchange}") String exchangeName
    ) {
        return new RabbitMQEventPublisher(rabbitTemplate, exchangeName);
    }
}
