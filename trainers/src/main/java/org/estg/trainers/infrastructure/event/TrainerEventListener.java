package org.estg.trainers.infrastructure.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.estg.trainers.domain.event.TrainerCreatedEvent;
import org.estg.trainers.domain.event.TrainerDeletedEvent;
import org.estg.trainers.domain.event.TrainerUpdatedEvent;

@Component
public class TrainerEventListener {

    private static final Logger log = LoggerFactory.getLogger(TrainerEventListener.class);

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "${rabbitmq.queue.trainer-created:trainer.created.queue}")
    public void onTrainerCreated(String message) {
        try {
            TrainerCreatedEvent event = objectMapper.readValue(message, TrainerCreatedEvent.class);
            log.info("Trainer Created Event received: TrainerId={}, Name={}", event.getTrainerId(),
                    event.getFullName());
        } catch (Exception e) {
            log.error("Error processing TrainerCreatedEvent", e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.trainer-updated:trainer.updated.queue}")
    public void onTrainerUpdated(String message) {
        try {
            TrainerUpdatedEvent event = objectMapper.readValue(message, TrainerUpdatedEvent.class);
            log.info("Trainer Updated Event received: TrainerId={}", event.getTrainerId());
        } catch (Exception e) {
            log.error("Error processing TrainerUpdatedEvent", e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.trainer-deleted:trainer.deleted.queue}")
    public void onTrainerDeleted(String message) {
        try {
            TrainerDeletedEvent event = objectMapper.readValue(message, TrainerDeletedEvent.class);
            log.info("Trainer Deleted Event received: TrainerId={}", event.getTrainerId());
        } catch (Exception e) {
            log.error("Error processing TrainerDeletedEvent", e);
        }
    }
}
