package org.estg.schedule.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.estg.schedule.dto.SessionDTO;
import org.estg.schedule.service.SessionService;

@SpringBootTest
@AutoConfigureMockMvc
class ScheduleControllerTest {

    private static final Logger log = LoggerFactory.getLogger(ScheduleControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SessionService sessionService;

    private SessionDTO sessionDTO;
    private String sessionId;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID().toString();
        sessionDTO = new SessionDTO();
        sessionDTO.setId(sessionId);
        sessionDTO.setMemberId("member123");
        sessionDTO.setTrainerId("trainer456");
        sessionDTO.setStartTime(LocalDateTime.now().plusHours(1));
        sessionDTO.setEndTime(LocalDateTime.now().plusHours(2));
    }

    @Test
    void testGetSessionByIdSuccess() throws Exception {
        when(sessionService.getSessionById(sessionId)).thenReturn(sessionDTO);

        mockMvc.perform(get("/api/sessions/{id}", sessionId))
                .andExpect(status().isOk());
    }

    @Test
    void testListSessionsEmpty() throws Exception {
        when(sessionService.listSessions(null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/sessions"))
                .andExpect(status().isOk());
    }

    @Test
    void testConfirmSessionSuccess() throws Exception {
        when(sessionService.confirmSession(sessionId)).thenReturn(sessionDTO);

        mockMvc.perform(post("/api/sessions/{id}/confirm", sessionId))
                .andExpect(status().isOk());
    }

    @Test
    void testCancelSessionSuccess() throws Exception {
        when(sessionService.cancelSession(sessionId, "reason")).thenReturn(sessionDTO);

        mockMvc.perform(post("/api/sessions/{id}/cancel", sessionId))
                .andExpect(status().isOk());
    }

    @AfterEach
    void banner() {
        log.info("? ? TESTE PASSOU COM SUCESSO ? ? ?");
    }
}
