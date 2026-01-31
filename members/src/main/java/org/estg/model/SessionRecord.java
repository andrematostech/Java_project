package org.estg.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "session_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Members member;

    @Column(nullable = false)
    private LocalDateTime sessionDateTime;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String trainerName;

    @Column(nullable = false)
    private String sessionType;

    @Column(nullable = false)
    private Boolean completed;

    // Basic domain validation used by service layer
    public void validate() {
        if (sessionDateTime == null) {
            throw new IllegalArgumentException("Session date/time is required");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (trainerName == null || trainerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Trainer name is required");
        }
        if (sessionType == null || sessionType.trim().isEmpty()) {
            throw new IllegalArgumentException("Session type is required");
        }
        if (completed == null) {
            throw new IllegalArgumentException("Completed flag is required");
        }
    }

    public void markAsCompleted() {
        this.completed = true;
    }

    public boolean isCompleted() {
        return Boolean.TRUE.equals(this.completed);
    }
}
