package org.estg.schedule.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.estg.schedule.data.SessionParticipantRepository;
import org.estg.schedule.data.SessionRepository;
import org.estg.schedule.dto.SessionDTO;
import org.estg.schedule.exceptions.SessionNotFoundException;
import org.estg.schedule.infrastructure.event.EventPublisher;
import org.estg.schedule.model.Session;

@SpringBootTest
class SessionServiceTest {

    private static final Logger log = LoggerFactory.getLogger(SessionServiceTest.class);

    @MockitoBean
    private SessionRepository sessionRepository;

    @MockitoBean
    private SessionParticipantRepository participantRepository;

    @MockitoBean
    private ModelMapper modelMapper;

    @MockitoBean
    private EventPublisher eventPublisher;

    @Autowired
    private SessionService sessionService;

    private Session mockSession;
    private SessionDTO mockSessionDTO;
    private String sessionId;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID().toString();
        mockSession = new Session();
        mockSession.setId(sessionId);
        mockSession.setMemberId("member123");
        mockSession.setTrainerId("trainer456");
        mockSession.setStartTime(LocalDateTime.now().plusHours(1));
        mockSession.setEndTime(LocalDateTime.now().plusHours(2));
        mockSession.setStatus(Session.SessionStatus.SCHEDULED);

        mockSessionDTO = new SessionDTO();
        mockSessionDTO.setId(sessionId);
        mockSessionDTO.setMemberId("member123");
        mockSessionDTO.setTrainerId("trainer456");
        mockSessionDTO.setStartTime(LocalDateTime.now().plusHours(1));
        mockSessionDTO.setEndTime(LocalDateTime.now().plusHours(2));
    }

    @Test
    void testGetSessionByIdSuccess() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
        when(modelMapper.map(mockSession, SessionDTO.class)).thenReturn(mockSessionDTO);

        SessionDTO result = sessionService.getSessionById(sessionId);

        assertNotNull(result);
        assertEquals(sessionId, result.getId());
    }

    @Test
    void testGetSessionByIdNotFound() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThrows(SessionNotFoundException.class, () -> sessionService.getSessionById(sessionId));
    }

    @Test
    void testConfirmSessionSuccess() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
        when(sessionRepository.save(any(Session.class))).thenReturn(mockSession);
        when(modelMapper.map(mockSession, SessionDTO.class)).thenReturn(mockSessionDTO);

        SessionDTO result = sessionService.confirmSession(sessionId);

        assertNotNull(result);
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    void testListSessionsByMemberId() {
        List<Session> sessions = List.of(mockSession);

        when(sessionRepository.findByMemberId("member123")).thenReturn(sessions);
        when(modelMapper.map(mockSession, SessionDTO.class)).thenReturn(mockSessionDTO);

        List<SessionDTO> result = sessionService.listSessions("member123", null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testListSessionsByTrainerId() {
        List<Session> sessions = List.of(mockSession);

        when(sessionRepository.findByTrainerId("trainer456")).thenReturn(sessions);
        when(modelMapper.map(mockSession, SessionDTO.class)).thenReturn(mockSessionDTO);

        List<SessionDTO> result = sessionService.listSessions(null, "trainer456");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @AfterEach
    void banner() {
        log.info("? ? TESTE PASSOU COM SUCESSO ? ? ?");
    }
}
