package org.estg.workout.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.estg.workout.dto.CreatePlanFromTemplateRequest;
import org.estg.workout.dto.WorkoutPlanRequest;
import org.estg.workout.model.WorkoutPlan;
import org.estg.workout.service.WorkoutPlanService;

import jakarta.validation.Valid;

// REST API for Workout Plans
@RestController
@RequestMapping("/api/workout-plans")
public class WorkoutPlanController {

    private final WorkoutPlanService service;

    public WorkoutPlanController(WorkoutPlanService service) {
        this.service = service;
    }

    // Create workout plan (manual/custom)
    @PostMapping("/create")
    public ResponseEntity<WorkoutPlan> create(@Valid @RequestBody WorkoutPlanRequest req) {
        WorkoutPlan plan = new WorkoutPlan();
        plan.setMemberId(req.getMemberId());
        plan.setTrainerId(req.getTrainerId());
        plan.setName(req.getName());
        plan.setGoal(req.getGoal());
        plan.setExercisesJson(req.getExercisesJson());
        plan.setStartDate(req.getStartDate());
        plan.setEndDate(req.getEndDate());
        plan.setStatus(req.getStatus());

        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(plan));
    }

    // Create workout plan from a predefined template
    @PostMapping("/from-template/{templateId}")
    public ResponseEntity<WorkoutPlan> createFromTemplate(@PathVariable UUID templateId,
                                                          @Valid @RequestBody CreatePlanFromTemplateRequest req) {
        WorkoutPlan created = service.createFromTemplate(templateId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Update workout plan
    @PutMapping("/{id}/update")
    public ResponseEntity<WorkoutPlan> update(@PathVariable UUID id,
                                              @Valid @RequestBody WorkoutPlanRequest req) {
        WorkoutPlan plan = new WorkoutPlan();
        plan.setTrainerId(req.getTrainerId());
        plan.setName(req.getName());
        plan.setGoal(req.getGoal());
        plan.setExercisesJson(req.getExercisesJson());
        plan.setStartDate(req.getStartDate());
        plan.setEndDate(req.getEndDate());
        plan.setStatus(req.getStatus());

        return ResponseEntity.ok(service.update(id, plan));
    }

    // Mark exercise as completed
    @PostMapping("/{id}/exercise-completed")
    public ResponseEntity<Void> exerciseCompleted(@PathVariable UUID id) {
        service.markExerciseCompleted(id);
        return ResponseEntity.accepted().build();
    }

    // Get workout plan by plan id (explicit path to avoid conflict with memberId
    // route)
    @GetMapping("/id/{id}")
    public ResponseEntity<WorkoutPlan> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // Get workout plans for member
    @GetMapping("/{memberId}")
    public ResponseEntity<List<WorkoutPlan>> getByMember(@PathVariable String memberId) {
        return ResponseEntity.ok(service.getAllByMember(memberId));
    }
}
