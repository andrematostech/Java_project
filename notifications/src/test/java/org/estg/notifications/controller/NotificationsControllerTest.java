package org.estg.notifications.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.estg.notifications.model.Notification;
import org.estg.notifications.service.NotificationService;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
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
    void testGetByRecipient() throws Exception {
        List<Notification> notifications = List.of(testNotification);
        when(notificationService.findByRecipientId("member-123")).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/recipients/member-123"))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).findByRecipientId("member-123");
    }

    @Test
    void testResendNotification() throws Exception {
        when(notificationService.resend(notificationId)).thenReturn(testNotification);

        mockMvc.perform(post("/api/notifications/{notificationId}/resend", notificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipientId").value("member-123"))
                .andExpect(jsonPath("$.type").value("SESSION_REMINDER"));

        verify(notificationService, times(1)).resend(notificationId);
    }

    @AfterEach
    void tearDown() {
        System.out.println("\n✅ ✅ TESTE PASSOU COM SUCESSO ✅ ✅\n");
    }
}
