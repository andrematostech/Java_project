package org.estg.workout.controller;

import org.estg.workout.model.WorkoutTemplate;
import org.estg.workout.service.WorkoutTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// REST API for browsing predefined workout templates
@RestController
@RequestMapping("/api/workout-templates")
public class WorkoutTemplateController {

    private final WorkoutTemplateService service;

    public WorkoutTemplateController(WorkoutTemplateService service) {
        this.service = service;
    }

    // List templates with optional filters
    @GetMapping
    public ResponseEntity<List<WorkoutTemplate>> list(
            @RequestParam(required = false) String goal,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String sex
    ) {
        return ResponseEntity.ok(service.list(goal, level, sex));
    }

    // Get template details
    @GetMapping("/{id}")
    public ResponseEntity<WorkoutTemplate> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(id));
    }
}
