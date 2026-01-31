package org.estg.schedule.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionDTO {
    private String id;
    private String memberId;
    private String trainerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String sessionType;
    private String status;
    private String sessionNotes;
    private Integer caloriesBurned;
    private String focusArea;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SessionParticipantDTO> participants;
}
