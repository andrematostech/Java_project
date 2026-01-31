package org.estg.trainers.dto;

import org.estg.trainers.model.CertificationStatus;
import org.estg.trainers.model.TrainerSpeciality;
import lombok.Data;

@Data
public class CreateTrainerRequest {

    private String fullName;
    private String email;
    private String phoneNumber;
    private TrainerSpeciality speciality;
    private Integer yearsExperience;
    private CertificationStatus certificationStatus;
    private String notes;
}
