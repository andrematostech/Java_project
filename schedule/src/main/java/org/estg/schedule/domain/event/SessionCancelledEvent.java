package org.estg.schedule.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SessionCancelledEvent extends SessionEvent {
    private String memberId;
    private String trainerId;
    private String reason;

    public SessionCancelledEvent(String sessionId, String memberId, String trainerId,
            String reason, LocalDateTime cancelledAt) {
        super(UUID.randomUUID().toString(), sessionId, cancelledAt, "SESSION_CANCELLED");
        this.memberId = memberId;
        this.trainerId = trainerId;
        this.reason = reason;
    }
}
