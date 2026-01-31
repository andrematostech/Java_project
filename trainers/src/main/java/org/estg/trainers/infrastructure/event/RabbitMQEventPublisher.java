package org.estg.trainers.infrastructure.event;
import org.estg.trainers.config.RabbitMQConfig;
import org.estg.trainers.domain.event.TrainerEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
/**
 * RabbitMQ publisher for Trainers bounded context.
 * Uses routing keys aligned with Trainers RabbitMQConfig bindings.
 */
public class RabbitMQEventPublisher implements EventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    public RabbitMQEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange.name:trainers-exchange}") String exchangeName
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
    }
    @Override
    public void publish(TrainerEvent event) {
        if (event == null) {
            return;
        }
        String routingKey = resolveRoutingKey(event);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
    }
    private String resolveRoutingKey(TrainerEvent event) {
        // Prefer the eventType if your domain event provides it (common pattern in your codebase).
        String eventType = event.getEventType();
        if (eventType != null) {
            String normalized = eventType.trim().toUpperCase();
            if (normalized.contains("CREATED")) {
                return RabbitMQConfig.ROUTING_CREATED;
            }
            if (normalized.contains("UPDATED")) {
                return RabbitMQConfig.ROUTING_UPDATED;
            }
            if (normalized.contains("DELETED") || normalized.contains("REMOVED")) {
                return RabbitMQConfig.ROUTING_DELETED;
            }
        }
        // Fallback: publish on a generic routing key (or choose one your bindings support).
        return RabbitMQConfig.ROUTING_UPDATED;
    }
}
