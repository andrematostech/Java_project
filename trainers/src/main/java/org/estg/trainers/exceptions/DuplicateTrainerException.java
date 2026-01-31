package org.estg.trainers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409 when email already exists
public class DuplicateTrainerException extends RuntimeException {

    public DuplicateTrainerException(String message) {
        super(message);
    }
}
