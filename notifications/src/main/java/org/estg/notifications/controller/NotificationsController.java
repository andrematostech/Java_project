package org.estg.notifications.controller;

import java.util.List;
import java.util.UUID;

import org.estg.notifications.model.Notification;
import org.estg.notifications.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {

    private final NotificationService notificationService;

    public NotificationsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/recipients/{recipientId}")
    public ResponseEntity<List<Notification>> getByRecipient(@PathVariable String recipientId) {
        return ResponseEntity.ok(notificationService.findByRecipientId(recipientId));
    }

    @PostMapping("/{notificationId}/resend")
    public ResponseEntity<Notification> resend(@PathVariable UUID notificationId) {
        return ResponseEntity.ok(notificationService.resend(notificationId));
    }
}

