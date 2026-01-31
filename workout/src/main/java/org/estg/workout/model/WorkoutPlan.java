package org.estg.workout.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workout_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String trainerId;

    @Column(nullable = false)
    private String name;

    private String goal;

    @Column(columnDefinition = "TEXT")
    private String exercisesJson;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    private String status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
