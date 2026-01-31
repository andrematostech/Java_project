package org.estg.trainers.data;

import org.estg.trainers.model.Trainer;
import org.estg.trainers.model.TrainerSpeciality;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TrainerRepository extends CrudRepository<Trainer, String> {

    boolean existsByEmail(String email);

    List<Trainer> findBySpeciality(TrainerSpeciality speciality);
}
