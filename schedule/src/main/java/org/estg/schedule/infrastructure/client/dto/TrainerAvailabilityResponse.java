package org.estg.schedule.infrastructure.client.dto;

public class TrainerAvailabilityResponse {

    private boolean available;

    public TrainerAvailabilityResponse() {
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
