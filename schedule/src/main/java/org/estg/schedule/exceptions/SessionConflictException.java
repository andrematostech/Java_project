package org.estg.schedule.exceptions;

public class SessionConflictException extends RuntimeException {
    public SessionConflictException(String message) {
        super(message);
    }
}
