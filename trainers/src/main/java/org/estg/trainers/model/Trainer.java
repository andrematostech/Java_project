package org.estg.trainers.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trainers")
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainerSpeciality speciality;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    // Keep nullable=true to avoid startup failure if DB already has NULLs from previous runs
    @Enumerated(EnumType.STRING)
    @Column(name = "certification_status", nullable = true)
    private CertificationStatus certificationStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainerStatus status;

    @Column
    private String notes;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = TrainerStatus.ACTIVE;
        }
        if (certificationStatus == null) {
            certificationStatus = CertificationStatus.PENDING;
        }
    }
}
