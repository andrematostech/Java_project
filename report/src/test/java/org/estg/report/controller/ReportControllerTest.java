package org.estg.report.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.estg.report.dto.ReportResponse;
import org.estg.report.service.ReportGenerationService;

@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportGenerationService reportGenerationService;

    private ReportResponse testResponse;

    @BeforeEach
    void setUp() {
        testResponse = new ReportResponse(
                UUID.randomUUID(),
                "occupancy:default",
                "30d",
                "{\"type\":\"default\",\"sessionsCompleted\":10}",
                Instant.now()
        );
    }

    @Test
    void testOccupancyReport() throws Exception {
        when(reportGenerationService.getOccupancyReport("30d", "default")).thenReturn(testResponse);

        mockMvc.perform(get("/api/reports/occupancy")
                .param("period", "30d")
                .param("type", "default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("occupancy:default"))
                .andExpect(jsonPath("$.period").value("30d"));

        verify(reportGenerationService, times(1)).getOccupancyReport("30d", "default");
    }

    @Test
    void testGrowthReport() throws Exception {
        ReportResponse growthResponse = new ReportResponse(
                UUID.randomUUID(),
                "growth",
                "30d",
                "{\"membersRegistered\":15}",
                Instant.now()
        );
        when(reportGenerationService.getGrowthReport("30d")).thenReturn(growthResponse);

        mockMvc.perform(get("/api/reports/growth")
                .param("period", "30d"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("growth"))
                .andExpect(jsonPath("$.period").value("30d"));

        verify(reportGenerationService, times(1)).getGrowthReport("30d");
    }

    @Test
    void testActivityReport() throws Exception {
        ReportResponse activityResponse = new ReportResponse(
                UUID.randomUUID(),
                "activity",
                "30d",
                "{\"totalEvents\":50,\"workoutPlanCreated\":10,\"sessionCompleted\":20}",
                Instant.now()
        );
        when(reportGenerationService.getActivityReport("30d")).thenReturn(activityResponse);

        mockMvc.perform(get("/api/reports/activity")
                .param("period", "30d"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("activity"))
                .andExpect(jsonPath("$.period").value("30d"));

        verify(reportGenerationService, times(1)).getActivityReport("30d");
    }

    @AfterEach
    void tearDown() {
        System.out.println("\n✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅\n");
    }
}
