package org.estg.schedule.infrastructure.event;

import org.estg.schedule.domain.event.SessionCancelledEvent;
import org.estg.schedule.domain.event.SessionCompletedEvent;
import org.estg.schedule.domain.event.SessionRescheduledEvent;
import org.estg.schedule.domain.event.SessionScheduledEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;

/**
 * Publishes schedule-related domain events to RabbitMQ.
 */
public class RabbitMQEventPublisher implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;

    public RabbitMQEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange.name:schedule-exchange}") String exchangeName
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
    }

    /**
     * Publishes a schedule event to RabbitMQ.
     * Routes are determined by the event's runtime type.
     */
    @Override
    public void publish(Object event) {
        if (event == null) {
            return;
        }

        if (event instanceof SessionScheduledEvent e) {
            publish("schedule.session.scheduled", e);
            return;
        }
        if (event instanceof SessionRescheduledEvent e) {
            publish("schedule.session.rescheduled", e);
            return;
        }
        if (event instanceof SessionCancelledEvent e) {
            publish("schedule.session.cancelled", e);
            return;
        }
        if (event instanceof SessionCompletedEvent e) {
            publish("schedule.session.completed", e);
            return;
        }

        // If an unknown event arrives, publish it to a generic routing key.
        publish("schedule.event.unknown", event);
    }

    private void publish(String routingKey, Object payload) {
        if (payload == null) {
            return;
        }
        rabbitTemplate.convertAndSend(exchangeName, routingKey, payload);
    }
}
