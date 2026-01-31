package org.estg.trainers.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrainerDeletedEvent extends TrainerEvent {

    private String reason;

    public TrainerDeletedEvent(String trainerId, String reason, LocalDateTime deletedAt) {
        super(UUID.randomUUID().toString(), trainerId, deletedAt, "TRAINER_DELETED");
        this.reason = reason;
    }
}
