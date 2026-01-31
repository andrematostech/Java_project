package org.estg.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TrainingGoal {

    @Column(name = "training_goal")
    private String value;

    protected TrainingGoal() {
        // JPA
    }

    public TrainingGoal(String value) {
        if (value != null && value.trim().length() > 255) {
            throw new IllegalArgumentException("Training goal too long");
        }
        this.value = value != null ? value.trim() : null;
    }

    public String getValue() {
        return value;
    }
}