package org.estg.infrastructure.event;

import org.estg.domain.event.MemberEvent;

public interface EventPublisher {
    void publish(MemberEvent event);
}
