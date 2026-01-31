package org.estg.trainers.dto;

import org.estg.trainers.model.CertificationStatus;
import org.estg.trainers.model.Trainer;
import org.estg.trainers.model.TrainerSpeciality;
import org.estg.trainers.model.TrainerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDTO {

    private String id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private TrainerSpeciality speciality;
    private Integer yearsExperience;
    private CertificationStatus certificationStatus;
    private TrainerStatus status;
    private String notes;

    public static TrainerDTO fromEntity(Trainer trainer) {
        if (trainer == null) return null;

        return TrainerDTO.builder()
                .id(trainer.getId())
                .fullName(trainer.getFullName())
                .email(trainer.getEmail())
                .phoneNumber(trainer.getPhoneNumber())
                .speciality(trainer.getSpeciality())
                .yearsExperience(trainer.getYearsExperience())
                .certificationStatus(trainer.getCertificationStatus())
                .status(trainer.getStatus())
                .notes(trainer.getNotes())
                .build();
    }
}
