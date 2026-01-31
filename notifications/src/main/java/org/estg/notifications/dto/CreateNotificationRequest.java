package org.estg.notifications.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body to create a notification.
 */
public class CreateNotificationRequest {

    @NotBlank // required
    private String recipientId;

    @NotBlank // required
    private String type;

    @NotBlank // required
    @Size(max = 500) // keep it short
    private String message;

    // getters/setters
    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
