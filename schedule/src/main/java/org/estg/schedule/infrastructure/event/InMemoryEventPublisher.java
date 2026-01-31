package org.estg.schedule.infrastructure.event;

import org.springframework.context.ApplicationEventPublisher;

// In-memory publisher for local/dev runs where RabbitMQ is NOT used
public class InMemoryEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher publisher;

    public InMemoryEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(Object event) {
        Object eventToPublish = (event == null) ? new Object() : event;
        publisher.publishEvent(eventToPublish);
    }
}
