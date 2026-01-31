package org.estg.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    @Column(name = "address_line")
    private String line;

    @Column(name = "city")
    private String city;

    @Column(name = "zip_code")
    private String zipCode;

    protected Address() {
        // JPA
    }

    public Address(String line, String city, String zipCode) {
        this.line = line != null ? line.trim() : null;
        this.city = city != null ? city.trim() : null;
        this.zipCode = zipCode != null ? zipCode.trim() : null;
    }

    public String getLine() {
        return line;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }
}