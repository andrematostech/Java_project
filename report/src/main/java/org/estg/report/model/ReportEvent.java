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
@Table(name = "report_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportEvent {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    public static ReportEvent create(String eventType, Instant occurredAt) {
        ReportEvent e = new ReportEvent();
        e.id = UUID.randomUUID();
        e.eventType = eventType;
        e.occurredAt = occurredAt == null ? Instant.now() : occurredAt;
        return e;
    }
}

