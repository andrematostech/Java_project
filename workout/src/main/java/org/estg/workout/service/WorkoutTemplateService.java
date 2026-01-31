package org.estg.workout.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import org.estg.workout.data.WorkoutTemplateRepository;
import org.estg.workout.exceptions.ResourceNotFoundException;
import org.estg.workout.model.WorkoutTemplate;

// Business logic for workout templates
@Service
public class WorkoutTemplateService {

    private final WorkoutTemplateRepository repo;

    public WorkoutTemplateService(WorkoutTemplateRepository repo) {
        this.repo = repo;
    }

    // List templates with optional filters
    public List<WorkoutTemplate> list(String goal, String level, String sex) {
        if (goal != null && level != null && sex != null) {
            return repo.findByGoalAndLevelAndSexOrderByCreatedAtDesc(goal, level, sex);
        }
        return repo.findAll();
    }

    // Get a single template by id
    public WorkoutTemplate get(UUID id) {
        return repo.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Workout template not found"));
    }
}
