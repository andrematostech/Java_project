package org.estg.workout.service;

import java.util.List; // list
import java.util.Objects; // objects
import java.util.Optional; // optional
import java.util.UUID; // uuid

import org.springframework.stereotype.Service; // Spring service

import org.estg.workout.data.WorkoutPlanRepository; // DB access for workout plans
import org.estg.workout.data.WorkoutTemplateRepository; // DB access for templates
import org.estg.workout.domain.event.WorkoutEventPublisher; // RabbitMQ publisher (events)
import org.estg.workout.domain.event.WorkoutPlanEvent; // event payload
import org.estg.workout.dto.CreatePlanFromTemplateRequest; // request DTO
import org.estg.workout.exceptions.ResourceNotFoundException; // 404 exception
import org.estg.workout.model.WorkoutPlan; // entity
import org.estg.workout.model.WorkoutTemplate; // entity

/**
 * Business logic for Workout Plans.
 * - Sync validation: calls Members/Trainers APIs (proposal requirement)
 * - Async integration: publishes RabbitMQ events (architecture requirement)
 */
@Service // marks as Spring service
public class WorkoutPlanService {

    // event types (strings used by your publisher / consumers)
    private static final String EVENT_CREATED = "WORKOUT_PLAN_CREATED";
    private static final String EVENT_UPDATED = "WORKOUT_PLAN_UPDATED";
    private static final String EVENT_EXERCISE_COMPLETED = "EXERCISE_COMPLETED";

    private final WorkoutPlanRepository repo; // workout plans repository
    private final WorkoutTemplateRepository templateRepo; // templates repository
    private final WorkoutEventPublisher publisher; // event publisher (RabbitMQ)
    private final ExternalValidationService externalValidationService; // REST validation helper

    // Constructor injection (Spring will inject all dependencies)
    public WorkoutPlanService(WorkoutPlanRepository repo,
            WorkoutTemplateRepository templateRepo,
            WorkoutEventPublisher publisher,
            ExternalValidationService externalValidationService) {
        this.repo = repo; // set repo
        this.templateRepo = templateRepo; // set templateRepo
        this.publisher = publisher; // set publisher
        this.externalValidationService = externalValidationService; // set validator
    }

    /**
     * Create a new workout plan (manual/custom).
     * Validates member + trainer first (sync REST).
     */
    public WorkoutPlan create(WorkoutPlan plan) {

        // validate member exists and is ACTIVE (via Members API)
        externalValidationService.validateMemberActive(plan.getMemberId());

        // validate trainer exists and is ACTIVE (via Trainers API)
        externalValidationService.validateTrainerActive(plan.getTrainerId());

        // save plan in DB
        WorkoutPlan saved = repo.save(plan);

        // publish async event for other services (reports/notifications)
        publisher.publishPlanCreated(
                new WorkoutPlanEvent(saved.getId(), saved.getMemberId(), EVENT_CREATED));

        return saved; // return persisted plan
    }

    /**
     * Create a new workout plan from an existing template.
     * Validates member + trainer first (sync REST).
     */
    public WorkoutPlan createFromTemplate(UUID templateId, CreatePlanFromTemplateRequest req) {

        // load template or fail
        WorkoutTemplate template = templateRepo.findById(Objects.requireNonNull(templateId))
                .orElseThrow(() -> new ResourceNotFoundException("Workout template not found"));

        // build new plan from template + request fields
        WorkoutPlan plan = new WorkoutPlan(); // new entity
        plan.setMemberId(req.getMemberId()); // set member id
        plan.setTrainerId(req.getTrainerId()); // set trainer id
        plan.setName(template.getName()); // copy name
        plan.setGoal(template.getGoal()); // copy goal
        plan.setExercisesJson(template.getExercisesJson()); // copy exercises json
        plan.setStartDate(req.getStartDate()); // set start date
        plan.setEndDate(req.getEndDate()); // set end date
        plan.setStatus(req.getStatus()); // set status

        // validate member exists and is ACTIVE (via Members API)
        externalValidationService.validateMemberActive(plan.getMemberId());

        // validate trainer exists and is ACTIVE (via Trainers API)
        externalValidationService.validateTrainerActive(plan.getTrainerId());

        // save plan in DB
        WorkoutPlan saved = repo.save(plan);

        // publish async event
        publisher.publishPlanCreated(
                new WorkoutPlanEvent(saved.getId(), saved.getMemberId(), EVENT_CREATED));

        return saved; // return persisted plan
    }

    /**
     * Update an existing workout plan.
     * Note: memberId is not changed here, so we validate only trainerId (if
     * provided).
     */
    public WorkoutPlan update(UUID id, WorkoutPlan data) {

        // load existing plan or fail
        WorkoutPlan plan = repo.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Workout plan not found"));

        // if trainerId is being set/changed, validate trainer
        if (data.getTrainerId() != null && !data.getTrainerId().isBlank()) {
            externalValidationService.validateTrainerActive(data.getTrainerId());
        }

        // update fields
        plan.setTrainerId(data.getTrainerId()); // set trainer id
        plan.setName(data.getName()); // set name
        plan.setGoal(data.getGoal()); // set goal
        plan.setExercisesJson(data.getExercisesJson()); // set exercises json
        plan.setStartDate(data.getStartDate()); // set start date
        plan.setEndDate(data.getEndDate()); // set end date
        plan.setStatus(data.getStatus()); // set status

        // save updated plan
        WorkoutPlan saved = repo.save(plan);

        // publish update event
        publisher.publishPlanUpdated(
                new WorkoutPlanEvent(saved.getId(), saved.getMemberId(), EVENT_UPDATED));

        return saved; // return updated plan
    }

    /**
     * Publish "exercise completed" event.
     * No validation needed here (plan already exists).
     */
    public void markExerciseCompleted(UUID planId) {

        // load plan or fail
        WorkoutPlan plan = repo.findById(Objects.requireNonNull(planId))
                .orElseThrow(() -> new ResourceNotFoundException("Workout plan not found"));

        // publish exercise completed event
        publisher.publishExerciseCompleted(
                new WorkoutPlanEvent(plan.getId(), plan.getMemberId(), EVENT_EXERCISE_COMPLETED));
    }

    /**
     * Get latest plan for a member.
     */
    public Optional<WorkoutPlan> getLatestByMember(String memberId) {
        return repo.findTopByMemberIdOrderByCreatedAtDesc(memberId); // query repo
    }

    /**
     * Get all plans for a member.
     */
    public List<WorkoutPlan> getAllByMember(String memberId) {
        return repo.findByMemberIdOrderByCreatedAtDesc(memberId); // query repo
    }

    // Get workout plan by id
    public WorkoutPlan getById(UUID id) {
        final UUID planId = (UUID) Objects.requireNonNull((id == null) ? UUID.randomUUID() : id);
        return repo.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout plan not found: " + planId));
    }
}
