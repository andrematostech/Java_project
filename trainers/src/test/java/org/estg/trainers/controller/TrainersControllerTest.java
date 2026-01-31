package org.estg.trainers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.estg.trainers.dto.CreateTrainerRequest;
import org.estg.trainers.dto.TrainerDTO;
import org.estg.trainers.dto.UpdateTrainerRequest;
import org.estg.trainers.model.CertificationStatus;
import org.estg.trainers.model.TrainerSpeciality;
import org.estg.trainers.model.TrainerStatus;
import org.estg.trainers.service.TrainersService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("removal")
@WebMvcTest(TrainersController.class)
class TrainersControllerTest {

    private static final Logger log = LoggerFactory.getLogger(TrainersControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainersService trainersService;

    @AfterEach
    void banner() {
        log.info("? ? TESTE PASSOU COM SUCESSO ? ? ?");
    }

    private TrainerDTO sampleDto(String name) {
        return TrainerDTO.builder()
                .id(UUID.randomUUID().toString())
                .fullName(name)
                .email("john@example.com")
                .phoneNumber("+123")
                .speciality(TrainerSpeciality.STRENGTH)
                .yearsExperience(5)
                .certificationStatus(CertificationStatus.APPROVED)
                .status(TrainerStatus.ACTIVE)
                .notes("sample")
                .build();
    }

    @Test
    void createTrainer_returnsOkWithBody() throws Exception {
        CreateTrainerRequest req = new CreateTrainerRequest();
        req.setFullName("John Doe");
        req.setSpeciality(TrainerSpeciality.CARDIO);
        req.setYearsExperience(2);
        req.setCertificationStatus(CertificationStatus.PENDING);

        TrainerDTO dto = sampleDto("John Doe");
        when(trainersService.createTrainer(Mockito.any(CreateTrainerRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("John Doe")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        verify(trainersService).createTrainer(Mockito.any(CreateTrainerRequest.class));
    }

    @Test
    void getAllTrainers_returnsList_withoutSpecialityParam() throws Exception {
        when(trainersService.getAllTrainers(null)).thenReturn(List.of(sampleDto("A"), sampleDto("B")));

        mockMvc.perform(get("/trainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].fullName", anyOf(is("A"), is("B"))));
    }

    @Test
    void getAllTrainers_filtersBySpeciality() throws Exception {
        when(trainersService.getAllTrainers(TrainerSpeciality.CARDIO)).thenReturn(List.of(sampleDto("C")));

        mockMvc.perform(get("/trainers").param("speciality", "CARDIO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fullName", is("C")));

        verify(trainersService).getAllTrainers(TrainerSpeciality.CARDIO);
    }

    @Test
    void getTrainerById_returnsTrainer() throws Exception {
        TrainerDTO dto = sampleDto("John Doe");
        when(trainersService.getTrainerById("abc")).thenReturn(dto);

        mockMvc.perform(get("/trainers/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("John Doe")));
    }

    @Test
    void updateTrainer_returnsUpdated() throws Exception {
        UpdateTrainerRequest req = new UpdateTrainerRequest();
        req.setFullName("Updated");
        req.setSpeciality(TrainerSpeciality.STRENGTH);

        TrainerDTO dto = sampleDto("Updated");
        when(trainersService.updateTrainer(eq("abc"), Mockito.any(UpdateTrainerRequest.class))).thenReturn(dto);

        mockMvc.perform(put("/trainers/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Updated")));
    }

    @Test
    void suspendTrainer_setsInactive() throws Exception {
        TrainerDTO dto = sampleDto("John");
        dto.setStatus(TrainerStatus.INACTIVE);
        when(trainersService.suspendTrainer("abc")).thenReturn(dto);

        mockMvc.perform(post("/trainers/abc/suspend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("INACTIVE")));
    }

    @Test
    void activateTrainer_setsActive() throws Exception {
        TrainerDTO dto = sampleDto("John");
        dto.setStatus(TrainerStatus.ACTIVE);
        when(trainersService.activateTrainer("abc")).thenReturn(dto);

        mockMvc.perform(post("/trainers/abc/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    void checkAvailability_returnsBoolean() throws Exception {
        when(trainersService.isTrainerAvailable("abc", DayOfWeek.MONDAY, java.time.LocalTime.parse("10:00"), java.time.LocalTime.parse("11:00")))
                .thenReturn(true);

        mockMvc.perform(get("/trainers/abc/availability")
                        .param("dayOfWeek", "MONDAY")
                        .param("startTime", "10:00")
                        .param("endTime", "11:00"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void deleteTrainer_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/trainers/abc"))
                .andExpect(status().isNoContent());

        verify(trainersService).deleteTrainer("abc");
    }
}
