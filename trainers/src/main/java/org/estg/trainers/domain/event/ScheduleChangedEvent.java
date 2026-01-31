package org.estg.trainers.domain.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScheduleChangedEvent {

    private String trainerId;
    private String changeType; // ADDED, UPDATED, REMOVED
    private LocalDateTime changedAt;
}
