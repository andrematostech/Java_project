package org.estg.schedule.data;

import java.util.List;

import org.estg.schedule.model.SessionParticipant;
import org.estg.schedule.model.SessionParticipant.ParticipantStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionParticipantRepository extends JpaRepository<SessionParticipant, String> {

    List<SessionParticipant> findBySession_Id(String sessionId);

    boolean existsBySession_IdAndMemberId(String sessionId, String memberId);

    List<SessionParticipant> findByStatus(ParticipantStatus status);
}
