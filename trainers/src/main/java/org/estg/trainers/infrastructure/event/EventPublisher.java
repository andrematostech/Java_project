package org.estg.trainers.infrastructure.event;

import org.estg.trainers.domain.event.TrainerEvent;

public interface EventPublisher {
    void publish(TrainerEvent event);
}
