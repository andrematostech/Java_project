package org.estg.schedule.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SessionParticipant Entity - Represents a participant in a group session
 *
 * Used for tracking participants in GROUP and CLASS type sessions
 */
@Entity
@Table(name = "session_participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionParticipant {

    public enum ParticipantStatus {
        CONFIRMED, ATTENDED, ABSENT, CANCELLED
    }

    @Id
    @GeneratedValue
    @UuidGenerator
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(nullable = false)
    private String memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantStatus status = ParticipantStatus.CONFIRMED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();

    @Column
    private LocalDateTime attendedAt;

    /**
     * Mark participant as attended
     */
    public void markAsAttended() {
        if (this.status == ParticipantStatus.ATTENDED) {
            throw new IllegalStateException("Participant is already marked as attended");
        }

        this.status = ParticipantStatus.ATTENDED;
        this.attendedAt = LocalDateTime.now();
    }

    /**
     * Mark participant as absent
     */
    public void markAsAbsent() {
        if (this.status == ParticipantStatus.ATTENDED) {
            throw new IllegalStateException("Cannot mark as absent - participant already attended");
        }

        this.status = ParticipantStatus.ABSENT;
    }

    /**
     * Cancel participation
     */
    public void cancelParticipation() {
        if (this.status == ParticipantStatus.ATTENDED) {
            throw new IllegalStateException("Cannot cancel - participant already attended");
        }

        this.status = ParticipantStatus.CANCELLED;
    }

    /**
     * Validate participant data
     */
    public void validate() {
        if (this.memberId == null || this.memberId.isBlank()) {
            throw new IllegalStateException("Member ID is required");
        }

        if (this.status == null) {
            throw new IllegalStateException("Participant status is required");
        }
    }
}
