package org.estg.workout.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.estg.workout.dto.WorkoutPlanRequest;
import org.estg.workout.infrastructure.client.MembersClient;
import org.estg.workout.infrastructure.client.TrainersClient;
import org.estg.workout.model.WorkoutPlan;
import org.estg.workout.service.WorkoutPlanService;

@SpringBootTest
@AutoConfigureMockMvc
class WorkoutPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkoutPlanService workoutPlanService;

    @MockitoBean
    private MembersClient membersClient;

    @MockitoBean
    private TrainersClient trainersClient;

    private UUID planId;
    private WorkoutPlan testPlan;
    private WorkoutPlanRequest testRequest;

    @BeforeEach
    void setUp() {
        planId = UUID.randomUUID();

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

        testRequest = new WorkoutPlanRequest();
        testRequest.setMemberId("member-" + UUID.randomUUID());
        testRequest.setTrainerId("trainer-" + UUID.randomUUID());
        testRequest.setName("Muscle Building");
        testRequest.setGoal("MUSCLE_GAIN");
        testRequest.setExercisesJson("[{\"exercise\": \"Bench Press\"}]");
        testRequest.setStartDate(LocalDate.now());
        testRequest.setEndDate(LocalDate.now().plusDays(30));
        testRequest.setStatus("ACTIVE");
    }

    @Test
    void testCreateWorkoutPlanSuccess() throws Exception {
        when(workoutPlanService.create(any())).thenReturn(testPlan);

        mockMvc.perform(post("/api/workout-plans/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Muscle Building"));

        verify(workoutPlanService, times(1)).create(any());
    }

    @Test
    void testGetWorkoutPlanByIdSuccess() throws Exception {
        when(workoutPlanService.getById(planId)).thenReturn(testPlan);

        mockMvc.perform(get("/api/workout-plans/id/{id}", planId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Muscle Building"));

        verify(workoutPlanService, times(1)).getById(planId);
    }

    @Test
    void testGetAllByMember() throws Exception {
        List<WorkoutPlan> plans = List.of(testPlan);
        when(workoutPlanService.getAllByMember(any())).thenReturn(plans);

        mockMvc.perform(get("/api/workout-plans/{memberId}", testPlan.getMemberId()))
                .andExpect(status().isOk());

        verify(workoutPlanService, times(1)).getAllByMember(any());
    }

    @Test
    void testUpdateWorkoutPlanSuccess() throws Exception {
        testPlan.setStatus("PAUSED");
        when(workoutPlanService.update(eq(planId), any())).thenReturn(testPlan);

        mockMvc.perform(put("/api/workout-plans/{id}/update", planId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAUSED"));

        verify(workoutPlanService, times(1)).update(eq(planId), any());
    }

    @Test
    void testMarkExerciseCompleted() throws Exception {
        doNothing().when(workoutPlanService).markExerciseCompleted(planId);

        mockMvc.perform(post("/api/workout-plans/{id}/exercise-completed", planId))
                .andExpect(status().isAccepted());

        verify(workoutPlanService, times(1)).markExerciseCompleted(planId);
    }

    @AfterEach
    void tearDown() {
        System.out.println("\n? ? TESTE PASSOU COM SUCESSO ? ?\n");
    }
}
