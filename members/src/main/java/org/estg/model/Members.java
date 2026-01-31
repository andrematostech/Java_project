package org.estg.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.estg.domain.valueobject.Address;
import org.estg.domain.valueobject.Email;
import org.estg.domain.valueobject.PhoneNumber;
import org.estg.domain.valueobject.TrainingGoal;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Members {

    @Id
    @GeneratedValue
    @UuidGenerator
    private String id;

    @Column(nullable = false)
    private String fullName;

    @Embedded
    private Email email;

    private LocalDate dateOfBirth;

    @Embedded
    private PhoneNumber phoneNumber;

    @Embedded
    private TrainingGoal trainingGoal;

    private String experienceLevel;

    // Keep explicit column mapping to match the existing DB schema.
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "line", column = @Column(name = "address")),
            @AttributeOverride(name = "city", column = @Column(name = "city")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "zipCode"))
    })
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<SessionRecord> sessions = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum MemberStatus {
        ACTIVE, SUSPENDED, INACTIVE
    }

    // Activates the member
    public void activate() {
        if (this.status == MemberStatus.ACTIVE) {
            throw new IllegalStateException("Member is already active");
        }
        this.status = MemberStatus.ACTIVE;
        touch();
    }

    // Suspends the member
    public void suspend() {
        if (this.status == MemberStatus.SUSPENDED) {
            throw new IllegalStateException("Member is already suspended");
        }
        this.status = MemberStatus.SUSPENDED;
        touch();
    }

    // Deactivates the member (soft delete)
    public void deactivate() {
        this.status = MemberStatus.INACTIVE;
        touch();
    }

    // Adds a session to the member (only ACTIVE members can have sessions)
    public void addSession(SessionRecord session) {
        if (!isActive()) {
            throw new IllegalStateException("Cannot add session to inactive member");
        }
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        sessions.add(session);
        session.setMember(this);
        touch();
    }

    // Checks if the member is active
    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }

    // Validates member data
    public void validate() {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (email == null || email.getValue() == null || !email.getValue().contains("@")) {
            throw new IllegalArgumentException("Valid email is required");
        }
        if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }
    }

    // Updates timestamp
    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        validate();
    }
}
