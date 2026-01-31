package org.estg.workout.data;

import org.estg.workout.model.WorkoutTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// Repository for accessing workout templates
public interface WorkoutTemplateRepository extends JpaRepository<WorkoutTemplate, UUID> {

    // Find templates filtered by goal, level and sex
    List<WorkoutTemplate> findByGoalAndLevelAndSexOrderByCreatedAtDesc(
            String goal,
            String level,
            String sex
    );
}
