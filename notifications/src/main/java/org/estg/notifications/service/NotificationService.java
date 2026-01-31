package org.estg.notifications.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.estg.notifications.data.NotificationRepository;
import org.estg.notifications.dto.CreateNotificationRequest;
import org.estg.notifications.exceptions.NotificationNotFoundException;
import org.estg.notifications.model.Notification;
import org.springframework.stereotype.Service;

/**
 * Business logic for notifications.
 */
@Service
public class NotificationService {

    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    public Notification create(CreateNotificationRequest req) {
        Objects.requireNonNull(req, "CreateNotificationRequest cannot be null");

        Notification n = Notification.create(
                req.getRecipientId(),
                req.getType(),
                req.getMessage());

        return repo.save(n);
    }

    public List<Notification> findByRecipientId(String recipientId) {
        return repo.findByRecipientIdOrderByCreatedAtDesc(recipientId);
    }

    public Notification findById(UUID id) {
        Objects.requireNonNull(id, "Notification ID cannot be null");
        return repo.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
    }

    public Notification markAsRead(UUID id) {
        Notification n = findById(id);
        n.setRead(true);
        return repo.save(n);
    }

    public Notification resend(UUID notificationId) {
        Notification original = findById(notificationId);

        Notification resent = Notification.create(
                original.getRecipientId(),
                original.getType(),
                original.getMessage());

        return repo.save(resent);
    }
}
