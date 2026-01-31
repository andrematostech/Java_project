package org.estg.schedule.infrastructure.event;

/**
 * Publishes domain events (as objects) to the infrastructure event bus.
 */
public interface EventPublisher {
    void publish(Object event);
}
