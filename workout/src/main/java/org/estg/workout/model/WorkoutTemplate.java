package org.estg.workout.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

// Predefined workout template stored in DB
@Entity
@Table(name = "workout_templates")
public class WorkoutTemplate {

    // Template unique id
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Template name shown to trainers/members
    @Column(nullable = false)
    private String name;

    // Template main goal (muscle_gain, fat_loss, strength)
    @Column(nullable = false)
    private String goal;

    // Template difficulty level (beginner, intermediate, advanced)
    @Column(nullable = false)
    private String level;

    // Target (male, female, unisex)
    @Column(nullable = false)
    private String sex;

    // Training split type (full_body, ppl, upper_lower)
    @Column(nullable = false)
    private String splitType;

    // Days per week
    @Column(nullable = false)
    private Integer daysPerWeek;

    // Exercises stored as JSON string
    @Column(columnDefinition = "TEXT", nullable = false)
    private String exercisesJson;

    // Auto timestamp for creation
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // Auto timestamp for updates
    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public String getSplitType() { return splitType; }
    public void setSplitType(String splitType) { this.splitType = splitType; }

    public Integer getDaysPerWeek() { return daysPerWeek; }
    public void setDaysPerWeek(Integer daysPerWeek) { this.daysPerWeek = daysPerWeek; }

    public String getExercisesJson() { return exercisesJson; }
    public void setExercisesJson(String exercisesJson) { this.exercisesJson = exercisesJson; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
