package org.estg.trainers.data;

import org.estg.trainers.model.TrainerSchedule;
import org.springframework.data.repository.CrudRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface TrainerScheduleRepository extends CrudRepository<TrainerSchedule, String> {

    List<TrainerSchedule> findByTrainerIdAndStatus(String trainerId, TrainerSchedule.ScheduleStatus status);

    List<TrainerSchedule> findByTrainerId(String trainerId);

    List<TrainerSchedule> findByTrainerIdAndDayOfWeek(String trainerId, DayOfWeek dayOfWeek);

    List<TrainerSchedule> findByTrainerIdAndDayOfWeekAndStatus(String trainerId,
                                                               DayOfWeek dayOfWeek,
                                                               TrainerSchedule.ScheduleStatus status);

    void deleteByTrainerId(String trainerId);
}
