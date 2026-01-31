package org.estg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionRecordDTO {
    
    private Long id;
    private String memberId;
    private LocalDateTime sessionDateTime;
    private String description;
    private String trainerName;
    private String sessionType;
    private Boolean completed;
}
