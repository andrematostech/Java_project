package org.estg.trainers.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateAvailabilityRequest {

    @NotNull
    private Boolean available;

    private String notes;

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
