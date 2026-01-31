package org.estg.report.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API response DTO for report snapshots.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private UUID id;
    // Serialize the enum value as a String to simplify the REST payload.
    private String type;
    private String period;
    private String payloadJson;
    private Instant createdAt;
}
