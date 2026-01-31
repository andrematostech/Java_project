package org.estg.trainers.exceptions;

public class TrainerNotFoundException extends RuntimeException {

    public TrainerNotFoundException(String id) {
        super("Trainer not found: " + id);
    }

    public TrainerNotFoundException(Object id) {
        super("Trainer not found: " + String.valueOf(id));
    }
}
