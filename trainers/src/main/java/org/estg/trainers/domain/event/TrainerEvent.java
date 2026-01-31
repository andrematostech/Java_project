package org.estg.trainers.domain.event;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class TrainerEvent {
    private String eventId;
    private String trainerId;
    private LocalDateTime occurredOn;
    private String eventType;

    public TrainerEvent(String eventId, String trainerId, LocalDateTime occurredOn, String eventType) {
        this.eventId = eventId;
        this.trainerId = trainerId;
        this.occurredOn = occurredOn;
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }

    public String getTrainerId() {
        return trainerId;
    }
}
