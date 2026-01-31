package org.estg.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class MemberProfileUpdatedEvent extends MemberEvent {
    private final LocalDateTime updatedAt;

    public MemberProfileUpdatedEvent(String memberId, LocalDateTime updatedAt) {
        super(UUID.randomUUID().toString(), memberId, LocalDateTime.now(), "MEMBER_PROFILE_UPDATED");
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}