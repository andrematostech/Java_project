package org.estg.report.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.estg.report.data.ReportEventRepository;
import org.estg.report.data.ReportRepository;
import org.estg.report.dto.ReportResponse;
import org.estg.report.model.Report;
import org.springframework.stereotype.Service;

@Service
public class ReportGenerationService {

    private final ReportRepository reportRepository;
    private final ReportEventRepository eventRepository;

    public ReportGenerationService(ReportRepository reportRepository, ReportEventRepository eventRepository) {
        this.reportRepository = reportRepository;
        this.eventRepository = eventRepository;
    }

    public ReportResponse getOccupancyReport(String period, String type) {
        String p = normalizePeriod(period);
        String t = (type == null || type.isBlank()) ? "default" : type;

        Report existing = reportRepository.findTopByReportTypeAndPeriodOrderByGeneratedDateDesc("occupancy:" + t, p)
                .orElse(null);

        if (existing != null) {
            return toResponse(existing);
        }

        Instant since = sinceFromPeriod(p);
        long sessionsCompleted = eventRepository.countByTypeSince("SessionCompleted", since);

        String metrics = "{\"type\":\"" + escape(t) + "\",\"sessionsCompleted\":" + sessionsCompleted + "}";
        Report created = reportRepository.save(Report.create("occupancy:" + t, p, metrics));
        return toResponse(created);
    }

    public ReportResponse getGrowthReport(String period) {
        String p = normalizePeriod(period);

        Report existing = reportRepository.findTopByReportTypeAndPeriodOrderByGeneratedDateDesc("growth", p)
                .orElse(null);

        if (existing != null) {
            return toResponse(existing);
        }

        Instant since = sinceFromPeriod(p);
        long membersRegistered = eventRepository.countByTypeSince("MemberRegistered", since);

        String metrics = "{\"membersRegistered\":" + membersRegistered + "}";
        Report created = reportRepository.save(Report.create("growth", p, metrics));
        return toResponse(created);
    }

    public ReportResponse getActivityReport(String period) {
        String p = normalizePeriod(period);

        Report existing = reportRepository.findTopByReportTypeAndPeriodOrderByGeneratedDateDesc("activity", p)
                .orElse(null);

        if (existing != null) {
            return toResponse(existing);
        }

        Instant since = sinceFromPeriod(p);
        long allEvents = eventRepository.countAllSince(since);
        long workoutPlanCreated = eventRepository.countByTypeSince("WorkoutPlanCreated", since);
        long sessionCompleted = eventRepository.countByTypeSince("SessionCompleted", since);

        String metrics = "{\"totalEvents\":" + allEvents + ",\"workoutPlanCreated\":" + workoutPlanCreated
                + ",\"sessionCompleted\":" + sessionCompleted + "}";
        Report created = reportRepository.save(Report.create("activity", p, metrics));
        return toResponse(created);
    }

    private ReportResponse toResponse(Report r) {
        return new ReportResponse(r.getId(), r.getReportType(), r.getPeriod(), r.getMetrics(), r.getGeneratedDate());
    }

    private String normalizePeriod(String period) {
        if (period == null || period.isBlank()) {
            return "30d";
        }

        String p = period.trim().toLowerCase();
        if (p.endsWith("d")) {
            return p;
        }

        try {
            int days = Integer.parseInt(p);
            if (days <= 0) {
                return "30d";
            }
            return days + "d";
        } catch (Exception ex) {
            return "30d";
        }
    }

    private Instant sinceFromPeriod(String normalizedPeriod) {
        try {
            String n = normalizedPeriod.trim().toLowerCase();
            int days = Integer.parseInt(n.replace("d", ""));
            return Instant.now().minus(days, ChronoUnit.DAYS);
        } catch (Exception ex) {
            return Instant.now().minus(30, ChronoUnit.DAYS);
        }
    }

    private String escape(String v) {
        return v.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
