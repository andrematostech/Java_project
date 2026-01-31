package org.estg.trainers.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trainer_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private String startTime; // HH:mm format

    @Column(nullable = false)
    private String endTime; // HH:mm format

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum ScheduleStatus {
        AVAILABLE, UNAVAILABLE, ON_LEAVE
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = ScheduleStatus.AVAILABLE;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void validate() {
        if (dayOfWeek == null) {
            throw new IllegalArgumentException("Day of week is required");
        }
        if (startTime == null || startTime.isBlank()) {
            throw new IllegalArgumentException("Start time is required");
        }
        if (endTime == null || endTime.isBlank()) {
            throw new IllegalArgumentException("End time is required");
        }
        if (!isValidTimeFormat(startTime) || !isValidTimeFormat(endTime)) {
            throw new IllegalArgumentException("Time must be in HH:mm format");
        }
        if (isTimeAfter(startTime, endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    private boolean isValidTimeFormat(String time) {
        return time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
    }

    private boolean isTimeAfter(String time1, String time2) {
        return time1.compareTo(time2) >= 0;
    }
}
