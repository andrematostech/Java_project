package org.estg.schedule.exceptions;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(String sessionId) {
        super("Session with ID " + sessionId + " not found");
    }
}
