package org.estg.infrastructure.event;

import org.estg.domain.event.MemberEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * In-memory implementation of EventPublisher.
 * Used only in non-docker and non-k8s profiles.
 */
@Component
@Profile("!docker & !k8s")
public class InMemoryEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(InMemoryEventPublisher.class);

    @Override
    public void publish(MemberEvent event) {
        log.info("In-memory event published: {}", event != null ? event.getEventType() : "null");
    }
}
