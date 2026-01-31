package org.estg.infrastructure.event;

import org.estg.domain.event.MemberActivatedEvent;
import org.estg.domain.event.MemberEvent;
import org.estg.domain.event.MemberProfileUpdatedEvent;
import org.estg.domain.event.MemberRegisteredEvent;
import org.estg.domain.event.MemberSuspendedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"docker", "k8s"})
public class RabbitMQEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;

    public RabbitMQEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange.name:members-exchange}") String exchangeName
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
    }

    @Override
    public void publish(MemberEvent event) {
        if (event == null) {
            return;
        }

        String routingKey = mapRoutingKey(event);

        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);

        log.info("Members event published: routingKey={} eventType={}", routingKey, event.getEventType());
    }

    private String mapRoutingKey(MemberEvent event) {

        if (event instanceof MemberRegisteredEvent) {
            return "member.member_registered";
        }

        if (event instanceof MemberActivatedEvent) {
            return "member.member_activated";
        }

        if (event instanceof MemberProfileUpdatedEvent) {
            return "member.member_profile_updated";
        }

        if (event instanceof MemberSuspendedEvent) {
            return "member.member_suspended";
        }

        return "member.unknown";
    }
}
