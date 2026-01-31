package org.estg.trainers.service;

import org.estg.trainers.data.TrainerRepository;
import org.estg.trainers.data.TrainerScheduleRepository;
import org.estg.trainers.dto.CreateTrainerRequest;
import org.estg.trainers.dto.TrainerDTO;
import org.estg.trainers.dto.UpdateAvailabilityRequest;
import org.estg.trainers.dto.UpdateTrainerRequest;
import org.estg.trainers.exceptions.DuplicateTrainerException;
import org.estg.trainers.exceptions.TrainerNotFoundException;
import org.estg.trainers.model.CertificationStatus;
import org.estg.trainers.model.Trainer;
import org.estg.trainers.model.TrainerSchedule;
import org.estg.trainers.model.TrainerSpeciality;
import org.estg.trainers.model.TrainerStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainersService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final TrainerRepository trainerRepository;
    private final TrainerScheduleRepository scheduleRepository;

    public TrainersService(TrainerRepository trainerRepository, TrainerScheduleRepository scheduleRepository) {
        this.trainerRepository = trainerRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public List<TrainerDTO> getAllTrainers() {
        return getAllTrainers(null);
    }

    public List<TrainerDTO> getAllTrainers(TrainerSpeciality speciality) {
        List<TrainerDTO> result = new ArrayList<>();

        Iterable<Trainer> source;
        if (speciality == null) {
            source = trainerRepository.findAll();
        } else {
            source = trainerRepository.findBySpeciality(speciality);
        }

        for (Trainer t : source) {
            result.add(TrainerDTO.fromEntity(t));
        }

        return result;
    }

    public TrainerDTO getTrainerById(String id) {
        Trainer trainer = trainerRepository.findById(id).orElseThrow(() -> new TrainerNotFoundException(id));
        return TrainerDTO.fromEntity(trainer);
    }

    @Transactional
    public TrainerDTO createTrainer(CreateTrainerRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (trainerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateTrainerException("Trainer email already exists: " + request.getEmail());
        }

        Trainer trainer = new Trainer();
        trainer.setFullName(request.getFullName());
        trainer.setEmail(request.getEmail());
        trainer.setPhoneNumber(request.getPhoneNumber());
        trainer.setSpeciality(request.getSpeciality());
        trainer.setYearsExperience(request.getYearsExperience());
        trainer.setNotes(request.getNotes());

        trainer.setStatus(TrainerStatus.ACTIVE);

        if (request.getCertificationStatus() != null) {
            trainer.setCertificationStatus(request.getCertificationStatus());
        } else {
            trainer.setCertificationStatus(CertificationStatus.PENDING);
        }

        Trainer saved = trainerRepository.save(trainer);
        return TrainerDTO.fromEntity(saved);
    }

    @Transactional
    public TrainerDTO updateTrainer(String id, UpdateTrainerRequest request) {
        Trainer trainer = trainerRepository.findById(id).orElseThrow(() -> new TrainerNotFoundException(id));

        if (request.getFullName() != null) {
            trainer.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            trainer.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getSpeciality() != null) {
            trainer.setSpeciality(request.getSpeciality());
        }
        if (request.getYearsExperience() != null) {
            trainer.setYearsExperience(request.getYearsExperience());
        }
        if (request.getCertificationStatus() != null) {
            trainer.setCertificationStatus(request.getCertificationStatus());
        }
        if (request.getStatus() != null) {
            trainer.setStatus(request.getStatus());
        }
        if (request.getNotes() != null) {
            trainer.setNotes(request.getNotes());
        }

        Trainer saved = trainerRepository.save(trainer);
        return TrainerDTO.fromEntity(saved);
    }

    @Transactional
    public TrainerDTO certifyTrainer(String id) {
        Trainer trainer = trainerRepository.findById(id).orElseThrow(() -> new TrainerNotFoundException(id));
        trainer.setCertificationStatus(CertificationStatus.APPROVED);
        Trainer saved = trainerRepository.save(trainer);
        return TrainerDTO.fromEntity(saved);
    }

    @Transactional
    public TrainerDTO updateAvailability(String id, UpdateAvailabilityRequest request) {
        Trainer trainer = trainerRepository.findById(id).orElseThrow(() -> new TrainerNotFoundException(id));

        if (request.getAvailable() != null) {
            trainer.setStatus(Boolean.TRUE.equals(request.getAvailable()) ? TrainerStatus.ACTIVE : TrainerStatus.ON_LEAVE);
        }
        if (request.getNotes() != null) {
            trainer.setNotes(request.getNotes());
        }

        Trainer saved = trainerRepository.save(trainer);
        return TrainerDTO.fromEntity(saved);
    }

    @Transactional
    public TrainerDTO suspendTrainer(String id) {
        Trainer trainer = trainerRepository.findById(id).orElseThrow(() -> new TrainerNotFoundException(id));
        trainer.setStatus(TrainerStatus.INACTIVE);
        Trainer saved = trainerRepository.save(trainer);
        return TrainerDTO.fromEntity(saved);
    }

    @Transactional
    public TrainerDTO activateTrainer(String id) {
        Trainer trainer = trainerRepository.findById(id).orElseThrow(() -> new TrainerNotFoundException(id));
        trainer.setStatus(TrainerStatus.ACTIVE);
        Trainer saved = trainerRepository.save(trainer);
        return TrainerDTO.fromEntity(saved);
    }

    // RF-TRAIN-07 - verify availability in a period (ISO date-time)
    public boolean isTrainerAvailable(String trainerId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException("startTime and endTime are required");
        }
        if (!startDateTime.isBefore(endDateTime)) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }
        if (startDateTime.toLocalDate().isAfter(endDateTime.toLocalDate())
                || !startDateTime.toLocalDate().equals(endDateTime.toLocalDate())) {
            throw new IllegalArgumentException("startTime and endTime must be on the same day");
        }

        DayOfWeek dayOfWeek = startDateTime.getDayOfWeek();
        LocalTime startTime = startDateTime.toLocalTime();
        LocalTime endTime = endDateTime.toLocalTime();

        return isTrainerAvailable(trainerId, dayOfWeek, startTime, endTime);
    }

    // Internal helper (day + HH:mm) used by tests and validation logic
    public boolean isTrainerAvailable(String trainerId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow(() -> new TrainerNotFoundException(trainerId));

        if (trainer.getStatus() != TrainerStatus.ACTIVE) {
            return false;
        }
        if (dayOfWeek == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("dayOfWeek, startTime and endTime are required");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }

        List<TrainerSchedule> schedules = scheduleRepository.findByTrainerIdAndDayOfWeekAndStatus(
                trainerId,
                dayOfWeek,
                TrainerSchedule.ScheduleStatus.AVAILABLE
        );

        String reqStart = startTime.format(TIME_FORMATTER);
        String reqEnd = endTime.format(TIME_FORMATTER);

        // Available if the requested interval is fully contained in at least one AVAILABLE slot
        for (TrainerSchedule s : schedules) {
            String slotStart = s.getStartTime();
            String slotEnd = s.getEndTime();

            boolean contained = reqStart.compareTo(slotStart) >= 0 && reqEnd.compareTo(slotEnd) <= 0;
            if (contained) {
                return true;
            }
        }

        return false;
    }

    @Transactional
    public void deleteTrainer(String id) {
        if (!trainerRepository.existsById(id)) {
            throw new TrainerNotFoundException(id);
        }
        scheduleRepository.deleteByTrainerId(id);
        trainerRepository.deleteById(id);
    }
}
