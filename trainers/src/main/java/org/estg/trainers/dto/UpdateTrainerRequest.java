package org.estg.trainers.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.estg.trainers.model.CertificationStatus;
import org.estg.trainers.model.TrainerSpeciality;
import org.estg.trainers.model.TrainerStatus;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateTrainerRequest {

    private String fullName;
    private String phoneNumber;

    // Canonical field name is "speciality" (matches DB/entity field).
    // Accept both "speciality" and "specialty" from clients.
    @JsonProperty("speciality")
    @JsonAlias({ "specialty", "speciality" })
    private TrainerSpeciality speciality;

    private Integer yearsExperience;
    private CertificationStatus certificationStatus;
    private TrainerStatus status;
    private String notes;
}
