package org.estg.schedule.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.estg.schedule.data.SessionParticipantRepository;
import org.estg.schedule.data.SessionRepository;
import org.estg.schedule.domain.event.SessionCancelledEvent;
import org.estg.schedule.domain.event.SessionCompletedEvent;
import org.estg.schedule.domain.event.SessionRescheduledEvent;
import org.estg.schedule.domain.event.SessionScheduledEvent;
import org.estg.schedule.dto.SessionDTO;
import org.estg.schedule.dto.SessionParticipantDTO;
import org.estg.schedule.exceptions.SessionConflictException;
import org.estg.schedule.exceptions.SessionNotFoundException;
import org.estg.schedule.infrastructure.client.MembersClient;
import org.estg.schedule.infrastructure.client.TrainersClient;
import org.estg.schedule.infrastructure.event.EventPublisher;
import org.estg.schedule.model.Session;
import org.estg.schedule.model.SessionParticipant;

@Service
@Transactional
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SessionParticipantRepository participantRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private MembersClient membersClient;

    @Autowired
    private TrainersClient trainersClient;

    // Table 10 - POST /api/sessions/book
    public SessionDTO scheduleSession(SessionDTO sessionDTO) {
        Session session = modelMapper.map(sessionDTO, Session.class);

        // Required synchronous validations (Members + Trainers) before booking
        membersClient.requireActiveMember(session.getMemberId());
        trainersClient.requireActiveTrainer(session.getTrainerId());
        trainersClient.requireTrainerAvailable(session.getTrainerId(), session.getStartTime(), session.getEndTime());

        session.validate();

        // Schedule DB conflict protection (member and trainer overlap)
        if (!isMemberAvailable(session.getMemberId(), session.getStartTime(), session.getEndTime())) {
            throw new SessionConflictException("Member is not available for the selected time slot");
        }

        if (!isTrainerAvailable(session.getTrainerId(), session.getStartTime(), session.getEndTime())) {
            throw new SessionConflictException("Trainer has a conflicting session in schedule_db");
        }

        session.schedule();

        Session savedSession = sessionRepository.save(session);

        eventPublisher.publish(new SessionScheduledEvent(
                savedSession.getId(),
                savedSession.getMemberId(),
                savedSession.getTrainerId(),
                savedSession.getStartTime(),
                savedSession.getEndTime(),
                savedSession.getCreatedAt()));

        return modelMapper.map(savedSession, SessionDTO.class);
    }

    // Table 10 - GET /api/sessions/{id}
    @Transactional(readOnly = true)
    public SessionDTO getSessionById(@NonNull String sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        return modelMapper.map(session, SessionDTO.class);
    }

    // Table 10 - POST /api/sessions/{id}/confirm
    public SessionDTO confirmSession(@NonNull String sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        session.startSession();

        Session saved = sessionRepository.save(session);
        return modelMapper.map(saved, SessionDTO.class);
    }

    // Table 10 - PUT /api/sessions/{id}/reschedule
    public SessionDTO rescheduleSession(@NonNull String sessionId, SessionDTO sessionDTO) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        // Re-validate member + trainer + trainer availability for the new slot
        membersClient.requireActiveMember(session.getMemberId());
        trainersClient.requireActiveTrainer(session.getTrainerId());
        trainersClient.requireTrainerAvailable(session.getTrainerId(), sessionDTO.getStartTime(), sessionDTO.getEndTime());

        session.updateDetails(sessionDTO.getStartTime(), sessionDTO.getEndTime(), null, null);
        Session updated = sessionRepository.save(session);

        eventPublisher.publish(new SessionRescheduledEvent(
                updated.getId(),
                updated.getMemberId(),
                updated.getTrainerId(),
                updated.getStartTime(),
                updated.getEndTime(),
                updated.getUpdatedAt()));

        return modelMapper.map(updated, SessionDTO.class);
    }

    // Table 10 - POST /api/sessions/{id}/cancel
    public SessionDTO cancelSession(@NonNull String sessionId, String reason) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        session.cancelSession(reason != null ? reason : "Cancelled by user");
        Session cancelledSession = sessionRepository.save(session);

        eventPublisher.publish(new SessionCancelledEvent(
                cancelledSession.getId(),
                cancelledSession.getMemberId(),
                cancelledSession.getTrainerId(),
                cancelledSession.getSessionNotes(),
                cancelledSession.getUpdatedAt()));

        return modelMapper.map(cancelledSession, SessionDTO.class);
    }

    // Table 10 - POST /api/sessions/{id}/complete
    public SessionDTO completeSession(@NonNull String sessionId, Integer caloriesBurned, String sessionNotes) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        if (session.getStatus() == Session.SessionStatus.SCHEDULED) {
            session.startSession();
        } else if (session.getStatus() != Session.SessionStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Only IN_PROGRESS sessions can be completed. Current status: " + session.getStatus());
        }

        session.completeSession(caloriesBurned, sessionNotes);

        Session completedSession = sessionRepository.save(session);

        eventPublisher.publish(new SessionCompletedEvent(
                completedSession.getId(),
                completedSession.getMemberId(),
                completedSession.getTrainerId(),
                completedSession.getCaloriesBurned(),
                completedSession.getSessionNotes(),
                completedSession.getUpdatedAt()));

        return modelMapper.map(completedSession, SessionDTO.class);
    }

    @Transactional(readOnly = true)
    public List<SessionDTO> listSessions(String memberId, String trainerId) {
        List<Session> sessions;

        if (memberId != null && !memberId.isBlank()) {
            sessions = sessionRepository.findByMemberId(memberId);
        } else if (trainerId != null && !trainerId.isBlank()) {
            sessions = sessionRepository.findByTrainerId(trainerId);
        } else {
            sessions = sessionRepository.findAll();
        }

        return sessions.stream()
                .map(s -> modelMapper.map(s, SessionDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isTrainerAvailable(@NonNull String trainerId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Session> sessions = sessionRepository.findTrainerSessionsBetween(trainerId, startTime, endTime);
        return sessions.isEmpty();
    }

    @Transactional(readOnly = true)
    public boolean isMemberAvailable(@NonNull String memberId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Session> sessions = sessionRepository.findMemberSessionsBetween(memberId, startTime, endTime);
        return sessions.isEmpty();
    }

    @Transactional(readOnly = true)
    public List<SessionParticipantDTO> getSessionParticipants(@NonNull String sessionId) {
        List<SessionParticipant> participants = participantRepository.findBySession_Id(sessionId);
        return participants.stream()
                .map(p -> modelMapper.map(p, SessionParticipantDTO.class))
                .collect(Collectors.toList());
    }

    public SessionParticipantDTO addParticipant(@NonNull String sessionId, String memberId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        if (participantRepository.existsBySession_IdAndMemberId(sessionId, memberId)) {
            throw new SessionConflictException("Member is already a participant in this session");
        }

        SessionParticipant participant = new SessionParticipant();
        participant.setSession(session);
        participant.setMemberId(memberId);
        participant.validate();

        SessionParticipant savedParticipant = participantRepository.save(participant);
        session.addParticipant(savedParticipant);

        return modelMapper.map(savedParticipant, SessionParticipantDTO.class);
    }

    public void removeParticipant(@NonNull String sessionId, @NonNull String participantId) {
        SessionParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new SessionNotFoundException("Participant " + participantId + " not found"));

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId));

        session.removeParticipant(participant);
        SessionParticipant participantToDelete = (participant == null) ? new SessionParticipant() : participant;
        participantRepository.delete(participantToDelete);
    }
}
