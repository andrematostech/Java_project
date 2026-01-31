package org.estg.trainers.service;

import org.estg.trainers.data.TrainerRepository;
import org.estg.trainers.data.TrainerScheduleRepository;
import org.estg.trainers.dto.CreateTrainerRequest;
import org.estg.trainers.dto.TrainerDTO;
import org.estg.trainers.dto.UpdateTrainerRequest;
import org.estg.trainers.exceptions.ResourceNotFoundException;
import org.estg.trainers.model.CertificationStatus;
import org.estg.trainers.model.Trainer;
import org.estg.trainers.model.TrainerSchedule;
import org.estg.trainers.model.TrainerSpeciality;
import org.estg.trainers.model.TrainerStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainersServiceTest {

    private static final Logger log = LoggerFactory.getLogger(TrainersServiceTest.class);

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainerScheduleRepository scheduleRepository;

    @InjectMocks
    private TrainersService trainersService;

    private Trainer existingTrainer;

    @BeforeEach
    void setUp() {
        existingTrainer = new Trainer();
        existingTrainer.setId(UUID.randomUUID().toString());
        existingTrainer.setFullName("John Doe");
        existingTrainer.setEmail("john@example.com");
        existingTrainer.setPhoneNumber("+123456789");
        existingTrainer.setSpeciality(TrainerSpeciality.STRENGTH);
        existingTrainer.setYearsExperience(5);
        existingTrainer.setCertificationStatus(CertificationStatus.APPROVED);
        existingTrainer.setStatus(TrainerStatus.ACTIVE);
        existingTrainer.setNotes("Experienced strength coach");
    }

    @AfterEach
    void banner() {
        log.info("? ? TESTE PASSOU COM SUCESSO ? ? ?");
    }

    @Test
    void createTrainer_savesAndReturnsDto() {
        CreateTrainerRequest request = new CreateTrainerRequest();
        request.setFullName("Alice Smith");
        request.setEmail("alice@example.com");
        request.setPhoneNumber("+111222333");
        request.setSpeciality(TrainerSpeciality.CARDIO);
        request.setYearsExperience(3);
        request.setCertificationStatus(CertificationStatus.PENDING);
        request.setNotes("Cardio specialist");

        // Simula a geração de ID ao salvar
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> {
            Trainer t = invocation.getArgument(0);
            t.setId("mock-id-123");
            return t;
        });

        TrainerDTO dto = trainersService.createTrainer(request);

        assertNotNull(dto.getId());
        assertEquals("Alice Smith", dto.getFullName());
        assertEquals("alice@example.com", dto.getEmail());
        assertEquals("+111222333", dto.getPhoneNumber());
        assertEquals(TrainerSpeciality.CARDIO, dto.getSpeciality());
        assertEquals(3, dto.getYearsExperience());
        assertEquals(CertificationStatus.PENDING, dto.getCertificationStatus());
        assertEquals(TrainerStatus.ACTIVE, dto.getStatus());
        assertEquals("Cardio specialist", dto.getNotes());

        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository, times(1)).save(captor.capture());
        assertEquals(TrainerStatus.ACTIVE, captor.getValue().getStatus());
    }

    @Test
    void getAllTrainers_returnsAll_whenSpecialityNull() {
        Trainer t1 = existingTrainer;
        Trainer t2 = new Trainer();
        t2.setId(UUID.randomUUID().toString());
        t2.setFullName("Jane Roe");
        t2.setSpeciality(TrainerSpeciality.CARDIO);
        t2.setStatus(TrainerStatus.ACTIVE);

        when(trainerRepository.findAll()).thenReturn(List.of(t1, t2));

        List<TrainerDTO> result = trainersService.getAllTrainers(null);
        assertEquals(2, result.size());
        verify(trainerRepository, times(1)).findAll();
        verify(trainerRepository, never()).findBySpeciality(any());
    }

    @Test
    void getAllTrainers_filtersBySpeciality() {
        when(trainerRepository.findBySpeciality(TrainerSpeciality.STRENGTH)).thenReturn(List.of(existingTrainer));

        List<TrainerDTO> result = trainersService.getAllTrainers(TrainerSpeciality.STRENGTH);
        assertEquals(1, result.size());
        assertEquals(TrainerSpeciality.STRENGTH, result.get(0).getSpeciality());
        verify(trainerRepository, times(1)).findBySpeciality(TrainerSpeciality.STRENGTH);
    }

    @Test
    void getTrainerById_returnsTrainer_whenFound() {
        when(trainerRepository.findById(existingTrainer.getId())).thenReturn(Optional.of(existingTrainer));

        TrainerDTO dto = trainersService.getTrainerById(existingTrainer.getId());
        assertEquals(existingTrainer.getId(), dto.getId());
        assertEquals("John Doe", dto.getFullName());
    }

    @Test
    void getTrainerById_throwsNotFound_whenMissing() {
        when(trainerRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainersService.getTrainerById("missing"));
    }

    @Test
    void updateTrainer_updatesFields_whenPresent() {
        when(trainerRepository.findById(existingTrainer.getId())).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setFullName("John Updated");
        request.setPhoneNumber("+999");
        request.setSpeciality(TrainerSpeciality.CARDIO);
        request.setYearsExperience(10);
        request.setCertificationStatus(CertificationStatus.APPROVED);
        request.setStatus(TrainerStatus.ACTIVE);
        request.setNotes("Updated notes");

        TrainerDTO dto = trainersService.updateTrainer(existingTrainer.getId(), request);

        assertEquals("John Updated", dto.getFullName());
        assertEquals("+999", dto.getPhoneNumber());
        assertEquals(TrainerSpeciality.CARDIO, dto.getSpeciality());
        assertEquals(10, dto.getYearsExperience());
        assertEquals(CertificationStatus.APPROVED, dto.getCertificationStatus());
        assertEquals(TrainerStatus.ACTIVE, dto.getStatus());
        assertEquals("Updated notes", dto.getNotes());

        verify(trainerRepository, times(1)).save(any(Trainer.class));
    }

    @Test
    void suspendTrainer_setsInactive() {
        when(trainerRepository.findById(existingTrainer.getId())).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerDTO dto = trainersService.suspendTrainer(existingTrainer.getId());
        assertEquals(TrainerStatus.INACTIVE, dto.getStatus());
    }

    @Test
    void activateTrainer_setsActive() {
        existingTrainer.setStatus(TrainerStatus.INACTIVE);
        when(trainerRepository.findById(existingTrainer.getId())).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerDTO dto = trainersService.activateTrainer(existingTrainer.getId());
        assertEquals(TrainerStatus.ACTIVE, dto.getStatus());
    }

    @Test
    void isTrainerAvailable_returnsFalse_whenTrainerInactive() {
        existingTrainer.setStatus(TrainerStatus.INACTIVE);
        when(trainerRepository.findById(existingTrainer.getId())).thenReturn(Optional.of(existingTrainer));

        boolean available = trainersService.isTrainerAvailable(existingTrainer.getId(), DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        assertFalse(available);
        verify(scheduleRepository, never()).findByTrainerIdAndDayOfWeekAndStatus(anyString(), any(), any());
    }

    @Test
    void isTrainerAvailable_returnsFalse_whenOverlapExists() {
        when(trainerRepository.findById(existingTrainer.getId())).thenReturn(Optional.of(existingTrainer));

        TrainerSchedule s = new TrainerSchedule();
        s.setTrainer(existingTrainer);
        s.setDayOfWeek(DayOfWeek.MONDAY);
        s.setStartTime("09:00");
        s.setEndTime("10:30");
        s.setStatus(TrainerSchedule.ScheduleStatus.AVAILABLE);

        when(scheduleRepository.findByTrainerIdAndDayOfWeekAndStatus(existingTrainer.getId(), DayOfWeek.MONDAY, TrainerSchedule.ScheduleStatus.AVAILABLE))
                .thenReturn(List.of(s));

        boolean available = trainersService.isTrainerAvailable(existingTrainer.getId(), DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        assertFalse(available);
    }

    @Test
    void isTrainerAvailable_returnsTrue_whenNoOverlap() {
        when(trainerRepository.findById(existingTrainer.getId())).thenReturn(Optional.of(existingTrainer));

        TrainerSchedule s = new TrainerSchedule();
        s.setTrainer(existingTrainer);
        s.setDayOfWeek(DayOfWeek.MONDAY);
        s.setStartTime("08:00");
        s.setEndTime("09:00");
        s.setStatus(TrainerSchedule.ScheduleStatus.AVAILABLE);

        when(scheduleRepository.findByTrainerIdAndDayOfWeekAndStatus(existingTrainer.getId(), DayOfWeek.MONDAY, TrainerSchedule.ScheduleStatus.AVAILABLE))
                .thenReturn(List.of(s));

        boolean available = trainersService.isTrainerAvailable(existingTrainer.getId(), DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
        assertTrue(available);
    }

    @Test
    void deleteTrainer_throwsNotFound_whenMissing() {
        when(trainerRepository.existsById("missing")).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> trainersService.deleteTrainer("missing"));
        verify(scheduleRepository, never()).deleteByTrainerId(anyString());
        verify(trainerRepository, never()).deleteById(anyString());
    }

    @Test
    void deleteTrainer_deletes_whenExists() {
        when(trainerRepository.existsById(existingTrainer.getId())).thenReturn(true);

        trainersService.deleteTrainer(existingTrainer.getId());

        verify(scheduleRepository, times(1)).deleteByTrainerId(existingTrainer.getId());
        verify(trainerRepository, times(1)).deleteById(existingTrainer.getId());
    }
}
