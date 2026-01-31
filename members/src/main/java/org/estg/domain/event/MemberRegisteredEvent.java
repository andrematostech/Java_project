package org.estg.domain.event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class MemberRegisteredEvent extends MemberEvent {

    private String email;
    private String fullName;
    private Instant occurredAt;

    public MemberRegisteredEvent(String memberId, String email, String fullName) {
        super(UUID.randomUUID().toString(), memberId, LocalDateTime.now(), "MEMBER_REGISTERED");
        this.email = email;
        this.fullName = fullName;
        this.occurredAt = LocalDateTime.now().toInstant(ZoneOffset.UTC);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}
