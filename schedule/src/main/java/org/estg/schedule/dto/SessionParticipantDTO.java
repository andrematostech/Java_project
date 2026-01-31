package org.estg.schedule.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionParticipantDTO {
    private String id;
    private String sessionId;
    private String memberId;
    private String status;
    private LocalDateTime registeredAt;
    private LocalDateTime attendedAt;
}
