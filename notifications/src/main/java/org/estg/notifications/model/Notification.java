package org.estg.notifications.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Notification entity stored in PostgreSQL.
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "recipient_id", nullable = false)
    private String recipientId;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "message", length = 500, nullable = false)
    private String message;

    @Column(name = "read", nullable = false)
    private boolean read;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // Factory for new notifications
    public static Notification create(String recipientId, String type, String message) {
        Notification n = new Notification();
        n.id = UUID.randomUUID();
        n.recipientId = recipientId;
        n.type = type;
        n.message = message;
        n.read = false;
        n.createdAt = Instant.now();
        return n;
    }
}
