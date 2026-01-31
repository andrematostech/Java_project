package org.estg.data;

import org.estg.model.Members;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MembersRepository extends JpaRepository<Members, String> {

    boolean existsByEmailValue(String emailValue);

    // Default list behavior: ACTIVE members only.
    Page<Members> findByStatus(Members.MemberStatus status, Pageable pageable);

    // ACTIVE members, optional trainingGoal filter
    @Query("""
            SELECT m
              FROM Members m
             WHERE m.status = 'ACTIVE'
               AND (:trainingGoal IS NULL OR :trainingGoal = '' OR m.trainingGoal.value = :trainingGoal)
            """)
    Page<Members> findByTrainingGoal(@Param("trainingGoal") String trainingGoal, Pageable pageable);
}
