package org.estg.report.controller;

import org.estg.report.dto.ReportResponse;
import org.estg.report.service.ReportGenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportGenerationService reportGenerationService;

    public ReportController(ReportGenerationService reportGenerationService) {
        this.reportGenerationService = reportGenerationService;
    }

    @GetMapping("/occupancy")
    public ResponseEntity<ReportResponse> occupancy(
            @RequestParam(name = "period", required = false) String period,
            @RequestParam(name = "type", required = false) String type) {

        return ResponseEntity.ok(reportGenerationService.getOccupancyReport(period, type));
    }

    @GetMapping("/growth")
    public ResponseEntity<ReportResponse> growth(
            @RequestParam(name = "period", required = false) String period) {

        return ResponseEntity.ok(reportGenerationService.getGrowthReport(period));
    }

    @GetMapping("/activity")
    public ResponseEntity<ReportResponse> activity(
            @RequestParam(name = "period", required = false) String period) {

        return ResponseEntity.ok(reportGenerationService.getActivityReport(period));
    }
}
