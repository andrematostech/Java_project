package org.estg.schedule.domain.event;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class SessionEvent {
    private String eventId;
    private String sessionId;
    private LocalDateTime occurredOn;
    private String eventType;

    public SessionEvent(String eventId, String sessionId, LocalDateTime occurredOn, String eventType) {
        this.eventId = eventId;
        this.sessionId = sessionId;
        this.occurredOn = occurredOn;
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }

    public String getSessionId() {
        return sessionId;
    }
}
