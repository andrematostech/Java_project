package org.estg.workout.data;

import org.estg.workout.model.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, UUID> {

    Optional<WorkoutPlan> findTopByMemberIdOrderByCreatedAtDesc(String memberId);

    List<WorkoutPlan> findByMemberIdOrderByCreatedAtDesc(String memberId);
}
