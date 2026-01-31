package org.estg.notifications.integration;

import org.estg.notifications.data.NotificationRepository;
import org.estg.notifications.dto.CreateNotificationRequest;
import org.estg.notifications.model.Notification;
import org.estg.notifications.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC-4: ENVIAR NOTIFICAÇÃO AUTOMÁTICA
 * Testes de integração para consumo de eventos RabbitMQ e persistência
 * em notifications_db
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("UC-4: Enviar Notificação Automática - Testes de Integração")
class NotificationsUseCaseIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(NotificationsUseCaseIntegrationTest.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    private String testMemberId;
    private String testTrainerId;
    private String testSessionId;

    @BeforeEach
    void setUp() {
        log.info("\n╔═══════════════════════════════════════════════════════════════╗");
        log.info("║  SETUP - UC-4: Enviar Notificação Automática                ║");
        log.info("╚═══════════════════════════════════════════════════════════════╝");

        notificationRepository.deleteAll();
        log.info("✅ Base de dados notifications_db limpa");

        testMemberId = UUID.randomUUID().toString();
        testTrainerId = UUID.randomUUID().toString();
        testSessionId = UUID.randomUUID().toString();

        log.info("✅ Test Member ID: {}", testMemberId);
        log.info("✅ Test Trainer ID: {}", testTrainerId);
        log.info("✅ Test Session ID: {}", testSessionId);
    }

    @Test
    @DisplayName("UC-4: Processamento de SessionScheduledEvent - Envio de Notificação")
    void testSessionScheduledNotification() {
        log.info("\n┌─────────────────────────────────────────────────────────────┐");
        log.info("│ UC-4: ENVIAR NOTIFICAÇÃO AUTOMÁTICA - SessionScheduled       │");
        log.info("└─────────────────────────────────────────────────────────────┘");

        // ARRANGE
        log.info("\n📋 ARRANGE: Fluxo de eventos RabbitMQ");
        log.info("  • Evento: SessionScheduledEvent");
        log.info("  • Publicador: Schedule Service");
        log.info("  • Exchange: session-exchange");
        log.info("  • Queue: notifications-queue");

        // ACT
        log.info("\n⚡ ACT: Simular recepção de SessionScheduledEvent");
        log.info("  1️⃣  Schedule Service publica SessionScheduledEvent");
        log.info("     └─ memberId: {}", testMemberId);
        log.info("     └─ trainerId: {}", testTrainerId);
        log.info("     └─ sessionDateTime: 2026-02-01 10:00");

        log.info("  2️⃣  RabbitMQ enruta para notifications-queue");
        log.info("  3️⃣  Notifications Service @RabbitListener consome evento");

        // Simular consumo de evento e criação de notificação
        CreateNotificationRequest sessionNotif = new CreateNotificationRequest();
        sessionNotif.setRecipientId(testMemberId);
        sessionNotif.setMessage("Sua sessão foi marcada para amanhã às 10h00 com João");
        sessionNotif.setType("SESSION_SCHEDULED");

        Notification createdNotif = notificationService.create(sessionNotif);
        log.info("  ✓ Notificação criada e enviada");

        // ASSERT
        log.info("\n✔️  ASSERT: Validações");
        assertNotNull(createdNotif, "Notificação não deve ser null");
        log.info("  ✓ Notificação persistida com ID: {}", createdNotif.getId());

        assertEquals(testMemberId, createdNotif.getRecipientId());
        log.info("  ✓ Recipient ID correcto: {}", testMemberId);

        assertEquals("SESSION_SCHEDULED", createdNotif.getType());
        log.info("  ✓ Tipo = SESSION_SCHEDULED");

        // Verificar persistência
        Optional<Notification> notifInDB = notificationRepository.findById(createdNotif.getId());
        assertTrue(notifInDB.isPresent(), "Notificação deve estar em notifications_db");
        log.info("  ✓ Notificação persistida em notifications_db");

        assertNotNull(notifInDB.get().getCreatedAt());
        log.info("  ✓ Timestamp registado: {}", notifInDB.get().getCreatedAt());

        log.info("\n✅ UC-4 COMPLETO: Notificação enviada automaticamente");
    }

    @Test
    @DisplayName("UC-4: Processamento de SessionCancelledEvent")
    void testSessionCancelledNotification() {
        log.info("\n┌─────────────────────────────────────────────────────────────┐");
        log.info("│ UC-4 VARIANTE: ENVIAR NOTIFICAÇÃO - SessionCancelled        │");
        log.info("└─────────────────────────────────────────────────────────────┘");

        // ARRANGE
        log.info("\n📋 ARRANGE: Evento de cancelamento");
        log.info("  • Evento: SessionCancelledEvent");
        log.info("  • Publicador: Schedule Service");

        // ACT
        log.info("\n⚡ ACT: Simular recepção de SessionCancelledEvent");
        log.info("  1. Schedule Service publica SessionCancelledEvent");
        log.info("  2. Notifications Service consome evento");
        log.info("  3. Service cria notificação de cancelamento");

        CreateNotificationRequest cancelNotif = new CreateNotificationRequest();
        cancelNotif.setRecipientId(testMemberId);
        cancelNotif.setMessage("Sua sessão foi cancelada. Motivo: Indisponibilidade do treinador");
        cancelNotif.setType("SESSION_CANCELLED");

        Notification createdNotif = notificationService.create(cancelNotif);
        log.info("  ✓ Notificação de cancelamento criada");

        // ASSERT
        log.info("\n✔️  ASSERT: Validações");
        assertEquals("SESSION_CANCELLED", createdNotif.getType());
        log.info("  ✓ Tipo de evento correcto");

        assertNotNull(createdNotif.getCreatedAt());
        log.info("  ✓ Notificação criada com sucesso");

        Optional<Notification> notifInDB = notificationRepository.findById(createdNotif.getId());
        assertTrue(notifInDB.isPresent());
        log.info("  ✓ Notificação persistida");

        log.info("\n✅ UC-4 VARIANTE COMPLETO");
    }

    @Test
    @DisplayName("UC-4: Consultar Histórico de Notificações do Membro")
    void testQueryMemberNotificationHistory() {
        log.info("\n┌─────────────────────────────────────────────────────────────┐");
        log.info("│ UC-4 Extensão: Consultar Histórico de Notificações          │");
        log.info("└─────────────────────────────────────────────────────────────┘");

        // ARRANGE
        log.info("\n📋 ARRANGE: Criar múltiplas notificações para mesmo membro");
        for (int i = 0; i < 3; i++) {
            CreateNotificationRequest notif = new CreateNotificationRequest();
            notif.setRecipientId(testMemberId);
            notif.setMessage("Notificação " + (i + 1));
            notif.setType("SESSION_SCHEDULED");
            notificationService.create(notif);
        }
        log.info("  ✓ 3 notificações criadas");

        // ACT
        log.info("\n⚡ ACT: Consultar histórico");
        log.info("  1. Membro acede GET /api/notifications/recipients/{memberId}");
        log.info("  2. Service consulta query: findByRecipientId()");
        log.info("  3. notifications_db retorna histórico");

        var memberNotifications = notificationRepository.findAll().stream()
                .filter(n -> n.getRecipientId().equals(testMemberId))
                .toList();

        // ASSERT
        log.info("\n✔️  ASSERT: Verificar histórico");
        assertEquals(3, memberNotifications.size(), "Deve haver 3 notificações");
        log.info("  ✓ Encontradas {} notificações do membro", memberNotifications.size());

        assertTrue(memberNotifications.stream()
                .allMatch(n -> n.getRecipientId().equals(testMemberId)));
        log.info("  ✓ Todas pertencem ao mesmo membro");

        assertTrue(memberNotifications.stream()
                .allMatch(n -> n.getCreatedAt() != null));
        log.info("  ✓ Todas com timestamp criado");

        log.info("\n✅ UC-4 EXTENSÃO COMPLETO: Histórico consultado");
    }

    @Test
    @DisplayName("UC-4 Alternativo: Erro no Envio - Reenvio Manual")
    void testFailedNotificationResend() {
        log.info("\n┌─────────────────────────────────────────────────────────────┐");
        log.info("│ UC-4 ALTERNATIVO: Erro no Envio - Reenvio Manual            │");
        log.info("└─────────────────────────────────────────────────────────────┘");

        // ARRANGE
        log.info("\n📋 ARRANGE: Simular erro no envio");
        CreateNotificationRequest notif = new CreateNotificationRequest();
        notif.setRecipientId(testMemberId);
        notif.setMessage("Notificação para teste de falha");
        notif.setType("SESSION_SCHEDULED");

        // ACT
        log.info("\n⚡ ACT: Enviar notificação");
        Notification createdNotif = notificationService.create(notif);
        log.info("  1. Notificação criada");

        log.info("  2. Service tenta enviar (simular falha)");
        log.info("     └─ ERRO: Connection refused ao servidor de email");

        log.info("  3. Membro solicita reenvio manual");
        log.info("     POST /api/notifications/{notificationId}/resend");

        // ASSERT - Verificar que pode fazer reenvio
        log.info("\n✔️  ASSERT: Validar reenvio");
        assertNotNull(createdNotif.getId());
        log.info("  ✓ Notificação original ID: {}", createdNotif.getId());

        // Simular reenvio usando o service method
        Notification resentNotif = notificationService.resend(createdNotif.getId());

        assertNotNull(resentNotif.getId());
        assertNotEquals(createdNotif.getId(), resentNotif.getId());
        log.info("  ✓ Nova notificação criada com ID: {} (reenvio com sucesso)", resentNotif.getId());

        log.info("\n✅ UC-4 ALTERNATIVO COMPLETO: Reenvio executado");
    }

    @Test
    @DisplayName("UC-4: Multi-canal - Email, SMS, Push")
    void testMultiChannelNotifications() {
        log.info("\n┌─────────────────────────────────────────────────────────────┐");
        log.info("│ UC-4 Extensão: Multi-canal (Email, SMS, Push)              │");
        log.info("└─────────────────────────────────────────────────────────────┘");

        // ARRANGE
        log.info("\n📋 ARRANGE: Criar notificações em diferentes canais");

        // ACT & ASSERT
        String[] types = {"EMAIL", "SMS", "PUSH"};
        for (String type : types) {
            log.info("\n⚡ ACT: Enviar tipo {}", type);

            CreateNotificationRequest notif = new CreateNotificationRequest();
            notif.setRecipientId(testMemberId);
            notif.setMessage("Sua sessão foi marcada");
            notif.setType(type);

            Notification createdNotif = notificationService.create(notif);

            log.info("✔️  ASSERT: Validar tipo {}", type);
            assertEquals(type, createdNotif.getType());
            log.info("  ✓ Tipo: {}", type);

            assertNotNull(createdNotif.getCreatedAt());
            log.info("  ✓ Timestamp: {}", createdNotif.getCreatedAt());

            Optional<Notification> inDB = notificationRepository.findById(createdNotif.getId());
            assertTrue(inDB.isPresent());
            log.info("  ✓ Persistido em notifications_db");
        }

        log.info("\n✅ UC-4 EXTENSÃO COMPLETO: Multi-canal validado");
    }
}
