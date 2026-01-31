package org.estg.schedule.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Session Domain Entity - Represents a training session
 *
 * DDD - Core domain entity with business logic
 * Manages session lifecycle from booking to completion
 */
@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    public enum SessionStatus {
        SCHEDULED, COMPLETED, CANCELLED, IN_PROGRESS
    }

    public enum SessionType {
        PERSONAL, GROUP, CLASS
    }

    // ========== ENTITY FIELDS ==========

    @Id
    @GeneratedValue
    @UuidGenerator
    private String id;

    @Column(nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String trainerId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionType sessionType = SessionType.PERSONAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.SCHEDULED;

    @Column
    private String sessionNotes;

    @Column
    private Integer caloriesBurned;

    @Column
    private String focusArea;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<SessionParticipant> participants = new ArrayList<>();

    // ========== DOMAIN LOGIC (DDD) ==========

    /**
     * Schedule a new session
     * Validates that session times are valid
     */
    public void schedule() {
        if (this.status != SessionStatus.SCHEDULED) {
            throw new IllegalStateException("Session must be in SCHEDULED status to schedule");
        }

        if (this.startTime == null || this.endTime == null) {
            throw new IllegalStateException("Start and end times are required");
        }

        if (this.startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Start time cannot be in the past");
        }

        if (this.endTime.isBefore(this.startTime)) {
            throw new IllegalStateException("End time must be after start time");
        }

        touch();
    }

    /**
     * Mark session as in progress
     * Can only transition from SCHEDULED status
     */
    public void startSession() {
        if (this.status != SessionStatus.SCHEDULED) {
            throw new IllegalStateException("Only SCHEDULED sessions can be started. Current status: " + this.status);
        }

        this.status = SessionStatus.IN_PROGRESS;
        touch();
    }

    /**
     * Complete the session
     * Can only transition from IN_PROGRESS status
     */
    public void completeSession(Integer caloriesBurned, String sessionNotes) {
        if (this.status != SessionStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Only IN_PROGRESS sessions can be completed. Current status: " + this.status);
        }

        this.status = SessionStatus.COMPLETED;
        this.caloriesBurned = caloriesBurned;
        this.sessionNotes = sessionNotes;
        touch();
    }

    /**
     * Cancel the session
     * Can be cancelled if not yet completed
     */
    public void cancelSession(String reason) {
        if (this.status == SessionStatus.COMPLETED || this.status == SessionStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel " + this.status.toString().toLowerCase() + " session");
        }

        this.status = SessionStatus.CANCELLED;
        this.sessionNotes = reason;
        touch();
    }

    /**
     * Update session details
     */
    public void updateDetails(LocalDateTime newStartTime, LocalDateTime newEndTime, String focusArea, String notes) {
        if (this.status == SessionStatus.COMPLETED || this.status == SessionStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update " + this.status.toString().toLowerCase() + " session");
        }

        if (newStartTime != null && newStartTime.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Start time cannot be in the past");
        }

        if (newStartTime != null && newEndTime != null && newEndTime.isBefore(newStartTime)) {
            throw new IllegalStateException("End time must be after start time");
        }

        if (newStartTime != null) {
            this.startTime = newStartTime;
        }
        if (newEndTime != null) {
            this.endTime = newEndTime;
        }
        if (focusArea != null && !focusArea.isBlank()) {
            this.focusArea = focusArea;
        }
        if (notes != null && !notes.isBlank()) {
            this.sessionNotes = notes;
        }

        touch();
    }

    /**
     * Add a participant to the session
     * Only for group sessions
     */
    public void addParticipant(SessionParticipant participant) {
        if (this.sessionType == SessionType.PERSONAL) {
            throw new IllegalStateException("Cannot add participants to PERSONAL sessions");
        }

        if (!this.participants.contains(participant)) {
            participant.setSession(this);
            this.participants.add(participant);
        }
    }

    /**
     * Remove a participant from the session
     */
    public void removeParticipant(SessionParticipant participant) {
        if (this.participants.remove(participant)) {
            participant.setSession(null);
        }
    }

    /**
     * Check if session is available for booking
     */
    public boolean isAvailable() {
        return this.status == SessionStatus.SCHEDULED;
    }

    /**
     * Validate session state before persistence
     */
    public void validate() {
        if (this.memberId == null || this.memberId.isBlank()) {
            throw new IllegalStateException("Member ID is required");
        }

        if (this.trainerId == null || this.trainerId.isBlank()) {
            throw new IllegalStateException("Trainer ID is required");
        }

        if (this.startTime == null || this.endTime == null) {
            throw new IllegalStateException("Session times are required");
        }

        if (this.startTime.isAfter(this.endTime)) {
            throw new IllegalStateException("Start time must be before end time");
        }

        if (this.sessionType == null) {
            throw new IllegalStateException("Session type is required");
        }
    }

    /**
     * Update the timestamp of last modification
     */
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}
