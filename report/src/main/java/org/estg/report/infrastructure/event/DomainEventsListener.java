package org.estg.report.infrastructure.event;

import java.time.Instant;

import org.estg.report.config.RabbitMQConfig;
import org.estg.report.data.ReportEventRepository;
import org.estg.report.model.ReportEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DomainEventsListener {

    private final ReportEventRepository eventRepository;

    public DomainEventsListener(ReportEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.MEMBER_REGISTERED_QUEUE)
    public void onMemberRegistered(Object payload) {
        // Persist a simplified event record for report aggregation
        eventRepository.save(ReportEvent.create("MemberRegistered", extractInstant(payload)));
    }

    @RabbitListener(queues = RabbitMQConfig.SESSION_COMPLETED_QUEUE)
    public void onSessionCompleted(Object payload) {
        // Persist a simplified event record for report aggregation
        eventRepository.save(ReportEvent.create("SessionCompleted", extractInstant(payload)));
    }

    @RabbitListener(queues = RabbitMQConfig.WORKOUT_PLAN_CREATED_QUEUE)
    public void onWorkoutPlanCreated(Object payload) {
        // Persist a simplified event record for report aggregation
        eventRepository.save(ReportEvent.create("WorkoutPlanCreated", extractInstant(payload)));
    }

    private Instant extractInstant(Object payload) {
        // Current implementation does not depend on payload shape
        return Instant.now();
    }
}
