package org.estg.schedule.domain.event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SessionCompletedEvent extends SessionEvent {

    private String memberId;
    private String trainerId;
    private Integer caloriesBurned;
    private String sessionNotes;
    private Instant occurredAt;

    public SessionCompletedEvent(
            String sessionId,
            String memberId,
            String trainerId,
            Integer caloriesBurned,
            String sessionNotes,
            LocalDateTime completedAt
    ) {
        super(UUID.randomUUID().toString(), sessionId, completedAt, "SESSION_COMPLETED");
        this.memberId = memberId;
        this.trainerId = trainerId;
        this.caloriesBurned = caloriesBurned;
        this.sessionNotes = sessionNotes;
        this.occurredAt = completedAt.toInstant(ZoneOffset.UTC);
    }
}
