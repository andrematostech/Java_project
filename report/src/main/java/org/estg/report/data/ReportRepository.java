package org.estg.report.data;

import java.util.Optional;
import java.util.UUID;

import org.estg.report.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, UUID> {

    Optional<Report> findTopByReportTypeAndPeriodOrderByGeneratedDateDesc(String reportType, String period);
}

