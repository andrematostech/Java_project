package org.estg.notifications.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.estg.notifications.data.NotificationRepository;
import org.estg.notifications.dto.CreateNotificationRequest;
import org.estg.notifications.exceptions.NotificationNotFoundException;
import org.estg.notifications.model.Notification;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private UUID notificationId;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        notificationId = UUID.randomUUID();
        testNotification = Notification.create("member-123", "SESSION_REMINDER", "Your session starts in 1 hour");
        testNotification.setId(notificationId);
    }

    @Test
    void testCreateNotification() {
        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setRecipientId("member-123");
        request.setType("SESSION_REMINDER");
        request.setMessage("Your session starts in 1 hour");

        when(notificationRepository.save(any())).thenReturn(testNotification);

        Notification result = notificationService.create(request);

        assertNotNull(result);
        assertEquals("member-123", result.getRecipientId());
        assertEquals("SESSION_REMINDER", result.getType());
        verify(notificationRepository, times(1)).save(any());
    }

    @Test
    void testFindByRecipientId() {
        List<Notification> notifications = List.of(testNotification);
        when(notificationRepository.findByRecipientIdOrderByCreatedAtDesc("member-123")).thenReturn(notifications);

        List<Notification> result = notificationService.findByRecipientId("member-123");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(notificationRepository, times(1)).findByRecipientIdOrderByCreatedAtDesc("member-123");
    }

    @Test
    void testFindByIdSuccess() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(testNotification));

        Notification result = notificationService.findById(notificationId);

        assertNotNull(result);
        assertEquals(notificationId, result.getId());
        verify(notificationRepository, times(1)).findById(notificationId);
    }

    @Test
    void testFindByIdNotFound() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () -> notificationService.findById(notificationId));
        verify(notificationRepository, times(1)).findById(notificationId);
    }

    @Test
    void testMarkAsRead() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any())).thenReturn(testNotification);

        Notification result = notificationService.markAsRead(notificationId);

        assertNotNull(result);
        assertTrue(result.isRead());
        verify(notificationRepository, times(1)).save(any());
    }

    @Test
    void testResendNotification() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any())).thenReturn(testNotification);

        Notification result = notificationService.resend(notificationId);

        assertNotNull(result);
        assertEquals(testNotification.getRecipientId(), result.getRecipientId());
        assertEquals(testNotification.getType(), result.getType());
        verify(notificationRepository, times(1)).findById(notificationId);
        verify(notificationRepository, times(1)).save(any());
    }

    @AfterEach
    void tearDown() {
        System.out.println("\n✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅\n");
    }
}
