package org.estg.domain.event;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class MemberEvent {
    private String eventId;
    private String memberId;
    private LocalDateTime occurredOn;
    private String eventType;

    public MemberEvent(String eventId, String memberId, LocalDateTime occurredOn, String eventType) {
        this.eventId = eventId;
        this.memberId = memberId;
        this.occurredOn = occurredOn;
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }

    public String getMemberId() {
        return memberId;
    }
}
