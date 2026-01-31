package org.estg.notifications.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Returned when a notification id does not exist.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotificationNotFoundException extends RuntimeException {

    public NotificationNotFoundException(UUID id) {
        super("Notification not found: " + id);
    }
}
