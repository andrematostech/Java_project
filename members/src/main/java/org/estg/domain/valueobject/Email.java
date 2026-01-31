package org.estg.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Email {
    @Column(name = "email", nullable = false, unique = true)
    private String value;

    protected Email() {
        // JPA
    }

    public Email(String value) {
        if (value == null || !value.contains("@")) {
            throw new IllegalArgumentException("Valid email is required");
        }
        this.value = value.trim();
    }

    public String getValue() {
        return value;
    }
}