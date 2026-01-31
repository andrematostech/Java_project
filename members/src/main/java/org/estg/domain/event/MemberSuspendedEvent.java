package org.estg.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class MemberSuspendedEvent extends MemberEvent {
    private String reason;

    public MemberSuspendedEvent(String memberId, String reason) {
        super(UUID.randomUUID().toString(), memberId, LocalDateTime.now(), "MEMBER_SUSPENDED");
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
