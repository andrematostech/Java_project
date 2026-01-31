package org.estg.workout.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

// Request body for creating a plan using a predefined template
public class CreatePlanFromTemplateRequest {

    // Member that will receive the plan
    @NotBlank
    private String memberId;

    // Trainer assigning the plan
    @NotBlank
    private String trainerId;

    // Start date of the plan
    @NotNull
    private LocalDate startDate;

    // End date of the plan
    @NotNull
    private LocalDate endDate;

    // Status for the created plan (e.g. ACTIVE)
    @NotBlank
    private String status;

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getTrainerId() { return trainerId; }
    public void setTrainerId(String trainerId) { this.trainerId = trainerId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
