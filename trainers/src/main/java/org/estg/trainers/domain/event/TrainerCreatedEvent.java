package org.estg.trainers.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrainerCreatedEvent extends TrainerEvent {

    private String fullName;
    private String email;
    private String speciality;

    public TrainerCreatedEvent(String trainerId, String fullName, String email, String speciality,
            LocalDateTime createdAt) {
        super(UUID.randomUUID().toString(), trainerId, createdAt, "TRAINER_CREATED");
        this.fullName = fullName;
        this.email = email;
        this.speciality = speciality;
    }
}
