package org.estg.report.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.estg.report.data.ReportEventRepository;
import org.estg.report.data.ReportRepository;
import org.estg.report.dto.ReportResponse;
import org.estg.report.model.Report;

@ExtendWith(MockitoExtension.class)
class ReportGenerationServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportEventRepository eventRepository;

    @InjectMocks
    private ReportGenerationService reportGenerationService;

    private Report testReport;

    @BeforeEach
    void setUp() {
        testReport = Report.create("occupancy:default", "30d", "{\"type\":\"default\",\"sessionsCompleted\":10}");
    }

    @Test
    void testGetOccupancyReportExisting() {
        when(reportRepository.findTopByReportTypeAndPeriodOrderByGeneratedDateDesc("occupancy:default", "30d"))
                .thenReturn(Optional.of(testReport));

        ReportResponse result = reportGenerationService.getOccupancyReport("30d", "default");

        assertNotNull(result);
        assertEquals("occupancy:default", result.getType());
        assertEquals("30d", result.getPeriod());
        verify(reportRepository, times(1)).findTopByReportTypeAndPeriodOrderByGeneratedDateDesc(any(), any());
        verify(reportRepository, never()).save(any());
    }

    @Test
    void testGetOccupancyReportNewGeneration() {
        when(reportRepository.findTopByReportTypeAndPeriodOrderByGeneratedDateDesc("occupancy:default", "30d"))
                .thenReturn(Optional.empty());
        when(eventRepository.countByTypeSince(eq("SessionCompleted"), any(Instant.class))).thenReturn(5L);
        when(reportRepository.save(any())).thenReturn(testReport);

        ReportResponse result = reportGenerationService.getOccupancyReport("30d", "default");

        assertNotNull(result);
        verify(eventRepository, times(1)).countByTypeSince(eq("SessionCompleted"), any());
        verify(reportRepository, times(1)).save(any());
    }

    @Test
    void testGetGrowthReportExisting() {
        Report growthReport = Report.create("growth", "30d", "{\"membersRegistered\":15}");
        when(reportRepository.findTopByReportTypeAndPeriodOrderByGeneratedDateDesc("growth", "30d"))
                .thenReturn(Optional.of(growthReport));

        ReportResponse result = reportGenerationService.getGrowthReport("30d");

        assertNotNull(result);
        assertEquals("growth", result.getType());
        assertEquals("30d", result.getPeriod());
        verify(reportRepository, never()).save(any());
    }

    @Test
    void testGetGrowthReportNewGeneration() {
        when(reportRepository.findTopByReportTypeAndPeriodOrderByGeneratedDateDesc("growth", "30d"))
                .thenReturn(Optional.empty());
        when(eventRepository.countByTypeSince(eq("MemberRegistered"), any(Instant.class))).thenReturn(20L);
        when(reportRepository.save(any())).thenReturn(testReport);

        ReportResponse result = reportGenerationService.getGrowthReport("30d");

        assertNotNull(result);
        verify(eventRepository, times(1)).countByTypeSince(eq("MemberRegistered"), any());
        verify(reportRepository, times(1)).save(any());
    }

    @Test
    void testGetActivityReportExisting() {
        Report activityReport = Report.create("activity", "30d", "{\"totalEvents\":50,\"workoutPlanCreated\":10,\"sessionCompleted\":20}");
        when(reportRepository.findTopByReportTypeAndPeriodOrderByGeneratedDateDesc("activity", "30d"))
                .thenReturn(Optional.of(activityReport));

        ReportResponse result = reportGenerationService.getActivityReport("30d");

        assertNotNull(result);
        assertEquals("activity", result.getType());
        assertEquals("30d", result.getPeriod());
        verify(reportRepository, never()).save(any());
    }

    @Test
    void testGetActivityReportNewGeneration() {
        when(reportRepository.findTopByReportTypeAndPeriodOrderByGeneratedDateDesc("activity", "30d"))
                .thenReturn(Optional.empty());
        when(eventRepository.countAllSince(any(Instant.class))).thenReturn(100L);
        when(eventRepository.countByTypeSince(eq("WorkoutPlanCreated"), any(Instant.class))).thenReturn(25L);
        when(eventRepository.countByTypeSince(eq("SessionCompleted"), any(Instant.class))).thenReturn(30L);
        when(reportRepository.save(any())).thenReturn(testReport);

        ReportResponse result = reportGenerationService.getActivityReport("30d");

        assertNotNull(result);
        verify(eventRepository, times(1)).countAllSince(any());
        verify(eventRepository, times(2)).countByTypeSince(any(), any());
        verify(reportRepository, times(1)).save(any());
    }

    @AfterEach
    void tearDown() {
        System.out.println("\n✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅\n");
    }
}
