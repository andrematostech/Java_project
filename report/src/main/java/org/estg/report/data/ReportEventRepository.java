package org.estg.report.data;

import java.time.Instant;
import java.util.UUID;

import org.estg.report.model.ReportEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportEventRepository extends JpaRepository<ReportEvent, UUID> {

    @Query("select count(e) from ReportEvent e where e.eventType = :eventType and e.occurredAt >= :since")
    long countByTypeSince(@Param("eventType") String eventType, @Param("since") Instant since);

    @Query("select count(e) from ReportEvent e where e.occurredAt >= :since")
    long countAllSince(@Param("since") Instant since);
}

