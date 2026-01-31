package org.estg.workout.service;

import org.estg.workout.config.ExternalServicesProperties;
import org.estg.workout.dto.MemberSummary;
import org.estg.workout.dto.TrainerSummary;
import org.estg.workout.infrastructure.client.MembersClient;
import org.estg.workout.infrastructure.client.TrainersClient;
import org.springframework.stereotype.Service;

/**
 * Validates memberId and trainerId using REST calls.
 * This matches the proposal: validate existence before creating plans.
 */
@Service // Spring service bean
public class ExternalValidationService {

    private final ExternalServicesProperties props; // config flag + URLs
    private final MembersClient membersClient;      // calls Members API
    private final TrainersClient trainersClient;    // calls Trainers API

    // Constructor injection
    public ExternalValidationService(ExternalServicesProperties props,
                                     MembersClient membersClient,
                                     TrainersClient trainersClient) {
        this.props = props;
        this.membersClient = membersClient;
        this.trainersClient = trainersClient;
    }

    /**
     * Validate that member exists and is ACTIVE.
     * Throws IllegalArgumentException on invalid data.
     */
    public void validateMemberActive(String memberId) {

        // skip validation if disabled in YAML
        if (!props.getValidation().isEnabled()) return;

        // call Members service
        MemberSummary member = membersClient.getById(memberId);

        // member must exist
        if (member == null) {
            throw new IllegalArgumentException("Member not found: " + memberId);
        }

        // member must be ACTIVE
        if (member.getStatus() == null || !"ACTIVE".equalsIgnoreCase(member.getStatus())) {
            throw new IllegalArgumentException("Member is not ACTIVE: " + memberId + " status=" + member.getStatus());
        }
    }

    /**
     * Validate that trainer exists and is ACTIVE.
     * Throws IllegalArgumentException on invalid data.
     */
    public void validateTrainerActive(String trainerId) {

        // skip validation if disabled in YAML
        if (!props.getValidation().isEnabled()) return;

        // call Trainers service
        TrainerSummary trainer = trainersClient.getById(trainerId);

        // trainer must exist
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer not found: " + trainerId);
        }

        // trainer must be ACTIVE
        if (trainer.getStatus() == null || !"ACTIVE".equalsIgnoreCase(trainer.getStatus())) {
            throw new IllegalArgumentException("Trainer is not ACTIVE: " + trainerId + " status=" + trainer.getStatus());
        }
    }
}
