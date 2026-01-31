package org.estg.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class MemberActivatedEvent extends MemberEvent {

    public MemberActivatedEvent(String memberId) {
        super(UUID.randomUUID().toString(), memberId, LocalDateTime.now(), "MEMBER_ACTIVATED");
    }
}