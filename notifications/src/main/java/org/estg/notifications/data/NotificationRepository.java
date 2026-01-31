package org.estg.notifications.data;

import org.estg.notifications.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * DB access for notifications.
 */
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    // find notifications for a recipient ordered by newest
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(String recipientId);
}
