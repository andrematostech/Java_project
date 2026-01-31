package org.estg.schedule.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.estg.schedule.model.Session;
import org.estg.schedule.model.Session.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {

    List<Session> findByMemberId(String memberId);

    List<Session> findByTrainerId(String trainerId);

    List<Session> findByStatus(SessionStatus status);

    List<Session> findByMemberIdAndStatus(String memberId, SessionStatus status);

    List<Session> findByTrainerIdAndStatus(String trainerId, SessionStatus status);

    @Query("SELECT s FROM Session s WHERE s.startTime BETWEEN :start AND :end")
    List<Session> findSessionsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT s FROM Session s WHERE s.memberId = :memberId AND s.startTime BETWEEN :start AND :end")
    List<Session> findMemberSessionsBetween(@Param("memberId") String memberId,
                                            @Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);

    @Query("SELECT s FROM Session s WHERE s.trainerId = :trainerId AND s.startTime BETWEEN :start AND :end")
    List<Session> findTrainerSessionsBetween(@Param("trainerId") String trainerId,
                                             @Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);

    Optional<Session> findByIdAndMemberId(String id, String memberId);

    boolean existsByMemberIdAndTrainerIdAndStatus(String memberId, String trainerId, SessionStatus status);
}
