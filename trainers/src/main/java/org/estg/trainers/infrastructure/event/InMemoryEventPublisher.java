package org.estg.trainers.infrastructure.event;

import org.estg.trainers.domain.event.TrainerEvent;

/**
 * In-memory publisher for local/dev runs where RabbitMQ is NOT used.
 * Active only when neither "docker" nor "k8s" profiles are enabled.
 */
public class InMemoryEventPublisher implements EventPublisher {

    @Override
    public void publish(TrainerEvent event) {
        // Simple dev-friendly output
        System.out.println("[InMemoryEventPublisher] Event published: " + event);
    }
}
