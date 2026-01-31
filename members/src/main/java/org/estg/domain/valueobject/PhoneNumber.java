package org.estg.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PhoneNumber {
    @Column(name = "phone_number")
    private String value;

    protected PhoneNumber() {
        // JPA
    }

    public PhoneNumber(String value) {
        if (value != null && value.trim().length() < 5) {
            throw new IllegalArgumentException("Phone number is too short");
        }
        this.value = value != null ? value.trim() : null;
    }

    public String getValue() {
        return value;
    }
}