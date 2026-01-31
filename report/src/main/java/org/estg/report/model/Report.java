package org.estg.report.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "report_type", nullable = false)
    private String reportType;

    @Column(name = "period", nullable = false)
    private String period;

    @Column(name = "metrics", columnDefinition = "text", nullable = false)
    private String metrics;

    @Column(name = "generated_date", nullable = false)
    private Instant generatedDate;

    public static Report create(String reportType, String period, String metrics) {
        Report r = new Report();
        r.id = UUID.randomUUID();
        r.reportType = reportType;
        r.period = period;
        r.metrics = metrics;
        r.generatedDate = Instant.now();
        return r;
    }
}

