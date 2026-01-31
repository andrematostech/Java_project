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

// RabbitMQ consumers for Workout events
@Component
public class WorkoutEventsListener {

    private static final Logger log = LoggerFactory.getLogger(WorkoutEventsListener.class);

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    public WorkoutEventsListener(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    // Workout events -> notify the member
    @RabbitListener(queues = "${rabbitmq.queue.notifications-workout:notifications.workout.queue}")
    public void onWorkoutEvent(Message message) {
        if (message == null || message.getBody() == null) {
            return;
        }

        try {
            JsonNode root = objectMapper.readTree(message.getBody());
            String routingKey = message.getMessageProperties() == null ? "" : safeText(message.getMessageProperties().getReceivedRoutingKey());
            String eventType = safeText(root, "type");
            String planId = safeText(root, "planId");

            log.info("Received workout event. routingKey={} type={} planId={}", routingKey, eventType, planId);
            handleEvent(routingKey, root);
        } catch (Exception ex) {
            log.error("Failed to process workout event message", ex);
        }
    }

    private void handleEvent(String routingKey, JsonNode root) {
        String memberId = safeText(root, "memberId");
        String planId = safeText(root, "planId");

        if (memberId.isBlank()) {
            return;
        }

        if ("workout.plan.created".equalsIgnoreCase(routingKey)) {
            createForRecipient(memberId, "WORKOUT_PLAN_CREATED", "Workout plan created (" + planId + ").");
            return;
        }

        if ("workout.plan.updated".equalsIgnoreCase(routingKey)) {
            createForRecipient(memberId, "WORKOUT_PLAN_UPDATED", "Workout plan updated (" + planId + ").");
            return;
        }

        if ("workout.exercise.completed".equalsIgnoreCase(routingKey)) {
            createForRecipient(memberId, "WORKOUT_EXERCISE_COMPLETED", "Exercise completed for workout plan (" + planId + ").");
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
