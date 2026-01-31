package org.estg.notifications.infrastructure.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.estg.notifications.dto.CreateNotificationRequest;
import org.estg.notifications.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

// RabbitMQ consumers for Schedule events
@Component
public class ScheduleEventsListener {

    private static final Logger log = LoggerFactory.getLogger(ScheduleEventsListener.class);

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    public ScheduleEventsListener(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    // Schedule events -> notify the member
    @RabbitListener(queues = "${rabbitmq.queue.notifications-session:notifications.session.queue}")
    public void onScheduleEvent(Message message) {
        if (message == null || message.getBody() == null) {
            return;
        }

        try {
            JsonNode root = objectMapper.readTree(message.getBody());

            String routingKey = "";
            if (message.getMessageProperties() != null) {
                routingKey = safeText(message.getMessageProperties().getReceivedRoutingKey());
            }

            String eventType = safeText(root, "eventType");
            String sessionId = safeText(root, "sessionId");
            String memberId = safeText(root, "memberId");

            log.info("Received schedule event. routingKey={} eventType={} sessionId={} memberId={}",
                    routingKey, eventType, sessionId, memberId);

            handleEvent(routingKey, root);
        } catch (Exception ex) {
            log.error("Failed to process schedule event message", ex);
        }
    }

    private void handleEvent(String routingKey, JsonNode root) {
        String memberId = safeText(root, "memberId");
        String sessionId = safeText(root, "sessionId");

        if (memberId.isBlank()) {
            return;
        }

        if ("schedule.session.scheduled".equalsIgnoreCase(routingKey)) {
            createForRecipient(memberId, "SESSION_SCHEDULED", "Session scheduled (" + sessionId + ").");
            return;
        }

        if ("schedule.session.rescheduled".equalsIgnoreCase(routingKey)) {
            createForRecipient(memberId, "SESSION_RESCHEDULED", "Session rescheduled (" + sessionId + ").");
            return;
        }

        if ("schedule.session.cancelled".equalsIgnoreCase(routingKey)) {
            createForRecipient(memberId, "SESSION_CANCELLED", "Session cancelled (" + sessionId + ").");
        }
    }

    private String safeText(JsonNode root, String key) {
        if (root == null || key == null) {
            return "";
        }
        JsonNode v = root.get(key);
        if (v == null || v.isNull()) {
            return "";
        }
        if (v.isTextual()) {
            return v.asText("");
        }
        return v.toString();
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private void createForRecipient(String recipientId, String type, String message) {
        if (recipientId == null || recipientId.isBlank()) {
            return;
        }

        try {
            CreateNotificationRequest req = new CreateNotificationRequest();
            req.setRecipientId(recipientId);
            req.setType(type);
            req.setMessage(message);

            notificationService.create(req);

            log.info("Notification created. recipientId={} type={}", recipientId, type);
        } catch (Exception ex) {
            log.error("Failed to create notification. recipientId={} type={}", recipientId, type, ex);
        }
    }
}
