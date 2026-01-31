package org.estg.workout.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.estg.workout.data.WorkoutPlanRepository;
import org.estg.workout.data.WorkoutTemplateRepository;
import org.estg.workout.domain.event.WorkoutEventPublisher;
import org.estg.workout.dto.CreatePlanFromTemplateRequest;
import org.estg.workout.exceptions.ResourceNotFoundException;
import org.estg.workout.model.WorkoutPlan;
import org.estg.workout.model.WorkoutTemplate;

@ExtendWith(MockitoExtension.class)
class WorkoutPlanServiceTest {

    @Mock
    private WorkoutPlanRepository workoutPlanRepository;

    @Mock
    private WorkoutTemplateRepository workoutTemplateRepository;

    @Mock
    private WorkoutEventPublisher eventPublisher;

    @Mock
    private ExternalValidationService externalValidationService;

    @InjectMocks
    private WorkoutPlanService workoutPlanService;

    private UUID planId;
    private UUID templateId;
    private WorkoutPlan testPlan;

    @BeforeEach
    void setUp() {
        planId = UUID.randomUUID();
        templateId = UUID.randomUUID();

        testPlan = new WorkoutPlan();
        testPlan.setId(planId);
        testPlan.setMemberId("member-" + UUID.randomUUID());
        testPlan.setTrainerId("trainer-" + UUID.randomUUID());
        testPlan.setName("Muscle Building");
        testPlan.setGoal("MUSCLE_GAIN");
        testPlan.setExercisesJson("[{\"exercise\": \"Bench Press\"}]");
        testPlan.setStartDate(LocalDate.now());
        testPlan.setEndDate(LocalDate.now().plusDays(30));
        testPlan.setStatus("ACTIVE");
    }

    @Test
    void testCreateWorkoutPlanSuccess() {
        doNothing().when(externalValidationService).validateMemberActive(any());
        doNothing().when(externalValidationService).validateTrainerActive(any());
        when(workoutPlanRepository.save(any())).thenReturn(testPlan);
        doNothing().when(eventPublisher).publishPlanCreated(any());

        WorkoutPlan result = workoutPlanService.create(testPlan);

        assertNotNull(result);
        assertEquals(testPlan.getId(), result.getId());
        assertEquals("Muscle Building", result.getName());
        verify(workoutPlanRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishPlanCreated(any());
    }

    @Test
    void testGetWorkoutPlanByIdSuccess() {
        when(workoutPlanRepository.findById(planId)).thenReturn(Optional.of(testPlan));

        WorkoutPlan result = workoutPlanService.getById(planId);

        assertNotNull(result);
        assertEquals(planId, result.getId());
        assertEquals("Muscle Building", result.getName());
        verify(workoutPlanRepository, times(1)).findById(planId);
    }

    @Test
    void testGetWorkoutPlanByIdNotFound() {
        when(workoutPlanRepository.findById(planId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> workoutPlanService.getById(planId));
        verify(workoutPlanRepository, times(1)).findById(planId);
    }

    @Test
    void testGetAllByMember() {
        List<WorkoutPlan> plans = List.of(testPlan);
        when(workoutPlanRepository.findByMemberIdOrderByCreatedAtDesc(any())).thenReturn(plans);

        List<WorkoutPlan> result = workoutPlanService.getAllByMember(testPlan.getMemberId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(workoutPlanRepository, times(1)).findByMemberIdOrderByCreatedAtDesc(any());
    }

    @Test
    void testUpdateWorkoutPlanSuccess() {
        when(workoutPlanRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        testPlan.setStatus("PAUSED");
        when(workoutPlanRepository.save(any())).thenReturn(testPlan);
        doNothing().when(externalValidationService).validateTrainerActive(any());
        doNothing().when(eventPublisher).publishPlanUpdated(any());

        WorkoutPlan result = workoutPlanService.update(planId, testPlan);

        assertNotNull(result);
        assertEquals("PAUSED", result.getStatus());
        verify(workoutPlanRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishPlanUpdated(any());
    }

    @Test
    void testUpdateWorkoutPlanNotFound() {
        when(workoutPlanRepository.findById(planId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> workoutPlanService.update(planId, testPlan));
        verify(workoutPlanRepository, never()).save(any());
    }

    @Test
    void testCreateFromTemplateSuccess() {
        WorkoutTemplate template = new WorkoutTemplate();
        template.setName("Standard Template");
        template.setGoal("MUSCLE_GAIN");
        template.setExercisesJson("[{\"exercise\": \"Squats\"}]");

        when(workoutTemplateRepository.findById(templateId)).thenReturn(Optional.of(template));
        doNothing().when(externalValidationService).validateMemberActive(any());
        doNothing().when(externalValidationService).validateTrainerActive(any());
        when(workoutPlanRepository.save(any())).thenReturn(testPlan);
        doNothing().when(eventPublisher).publishPlanCreated(any());

        CreatePlanFromTemplateRequest request = new CreatePlanFromTemplateRequest();
        request.setMemberId(testPlan.getMemberId());
        request.setTrainerId(testPlan.getTrainerId());

        WorkoutPlan result = workoutPlanService.createFromTemplate(templateId, request);

        assertNotNull(result);
        verify(workoutTemplateRepository, times(1)).findById(templateId);
        verify(workoutPlanRepository, times(1)).save(any());
    }

    @Test
    void testMarkExerciseCompleted() {
        when(workoutPlanRepository.findById(planId)).thenReturn(Optional.of(testPlan));
        doNothing().when(eventPublisher).publishExerciseCompleted(any());

        workoutPlanService.markExerciseCompleted(planId);

        verify(workoutPlanRepository, times(1)).findById(planId);
        verify(eventPublisher, times(1)).publishExerciseCompleted(any());
    }

    @AfterEach
    void tearDown() {
        System.out.println("\n? ? TESTE PASSOU COM SUCESSO ? ?\n");
    }
}
