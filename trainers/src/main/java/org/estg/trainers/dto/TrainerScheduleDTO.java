package org.estg.trainers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerScheduleDTO {

    private String id;
    private String trainerId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String status;
}
