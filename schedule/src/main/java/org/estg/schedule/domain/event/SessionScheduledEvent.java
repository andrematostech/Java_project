package org.estg.schedule.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SessionScheduledEvent extends SessionEvent {
    private String memberId;
    private String trainerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public SessionScheduledEvent(String sessionId, String memberId, String trainerId,
            LocalDateTime startTime, LocalDateTime endTime, LocalDateTime createdAt) {
        super(UUID.randomUUID().toString(), sessionId, createdAt, "SESSION_SCHEDULED");
        this.memberId = memberId;
        this.trainerId = trainerId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
