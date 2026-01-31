package org.estg.data;

import java.util.List;

import org.estg.model.SessionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRecordRepository extends JpaRepository<SessionRecord, Long> {

    List<SessionRecord> findByMemberId(String memberId);

    List<SessionRecord> findByMemberIdOrderBySessionDateTimeDesc(String memberId);
}
