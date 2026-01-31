package org.estg.trainers.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrainerUpdatedEvent extends TrainerEvent {

    private String fullName;
    private String email;
    private String speciality;

    public TrainerUpdatedEvent(String trainerId, String fullName, String email, String speciality,
            LocalDateTime updatedAt) {
        super(UUID.randomUUID().toString(), trainerId, updatedAt, "TRAINER_UPDATED");
        this.fullName = fullName;
        this.email = email;
        this.speciality = speciality;
    }
}
