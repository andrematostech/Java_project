package org.estg.trainers.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.estg.trainers.data.TrainerRepository;
import org.estg.trainers.exceptions.TrainerNotFoundException;
import org.estg.trainers.model.CertificationStatus;
import org.estg.trainers.model.Trainer;

import lombok.extern.slf4j.Slf4j;

/**
 * Domain service for Trainer certification approval
 * Contains business logic for certification workflow
 */
@Service
@Transactional
@Slf4j
public class TrainerCertificationService {

    private final TrainerRepository trainerRepository;

    public TrainerCertificationService(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    /**
     * Approve trainer certification
     * Business rule: Only PENDING certifications can be approved
     */
    public void approveCertification(String trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId != null ? trainerId : "")
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found: " + trainerId));

        if (!trainer.getCertificationStatus().equals(CertificationStatus.PENDING)) {
            throw new IllegalStateException("Only PENDING certifications can be approved");
        }

        trainer.setCertificationStatus(CertificationStatus.APPROVED);
        trainerRepository.save(trainer);
        log.info("Trainer certification approved: {}", trainerId);
    }

    /**
     * Reject trainer certification
     */
    public void rejectCertification(String trainerId, String reason) {
        Trainer trainer = trainerRepository.findById(trainerId != null ? trainerId : "")
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found: " + trainerId));

        trainer.setCertificationStatus(CertificationStatus.REJECTED);
        trainer.setNotes("Rejection reason: " + reason);
        trainerRepository.save(trainer);
        log.info("Trainer certification rejected: {} - Reason: {}", trainerId, reason);
    }
}
