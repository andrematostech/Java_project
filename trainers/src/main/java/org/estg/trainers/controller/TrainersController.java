package org.estg.trainers.controller;

import jakarta.validation.Valid;
import org.estg.trainers.dto.AvailabilityResponse;
import org.estg.trainers.dto.CreateTrainerRequest;
import org.estg.trainers.dto.TrainerDTO;
import org.estg.trainers.dto.UpdateAvailabilityRequest;
import org.estg.trainers.dto.UpdateTrainerRequest;
import org.estg.trainers.service.TrainersService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trainers")
public class TrainersController {

    private final TrainersService trainersService;

    public TrainersController(TrainersService trainersService) {
        this.trainersService = trainersService;
    }

    // POST /api/trainers/register
    @PostMapping("/register")
    public ResponseEntity<TrainerDTO> registerTrainer(@Valid @RequestBody CreateTrainerRequest request) {
        TrainerDTO created = trainersService.createTrainer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/trainers
    @GetMapping
    public ResponseEntity<List<TrainerDTO>> getAllTrainers() {
        return ResponseEntity.ok(trainersService.getAllTrainers());
    }

    // GET /api/trainers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TrainerDTO> getTrainerById(@PathVariable String id) {
        return ResponseEntity.ok(trainersService.getTrainerById(id));
    }

    // PUT /api/trainers/{id}/update
    @PutMapping("/{id}/update")
    public ResponseEntity<TrainerDTO> updateTrainer(
            @PathVariable String id,
            @Valid @RequestBody UpdateTrainerRequest request
    ) {
        return ResponseEntity.ok(trainersService.updateTrainer(id, request));
    }

    // POST /api/trainers/{id}/certify
    @PostMapping("/{id}/certify")
    public ResponseEntity<TrainerDTO> certifyTrainer(@PathVariable String id) {
        return ResponseEntity.ok(trainersService.certifyTrainer(id));
    }

    // PUT /api/trainers/{id}/availability
    @PutMapping("/{id}/availability")
    public ResponseEntity<TrainerDTO> updateAvailability(
            @PathVariable String id,
            @Valid @RequestBody UpdateAvailabilityRequest request
    ) {
        return ResponseEntity.ok(trainersService.updateAvailability(id, request));
    }

    // GET /api/trainers/{id}/available?startTime=...&endTime=...
    @GetMapping("/{id}/available")
    public ResponseEntity<AvailabilityResponse> checkAvailability(
            @PathVariable String id,
            @RequestParam("startTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam("endTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    ) {
        boolean available = trainersService.isTrainerAvailable(id, startTime, endTime);
        return ResponseEntity.ok(new AvailabilityResponse(available));
    }
}
