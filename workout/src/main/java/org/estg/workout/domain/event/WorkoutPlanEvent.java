package org.estg.workout.domain.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class WorkoutPlanEvent implements Serializable {

    private String planId;
    private String memberId;
    private String type;
    private Instant occurredAt;

    public WorkoutPlanEvent() {
    }

    public WorkoutPlanEvent(UUID planId, String memberId, String type) {
        this.planId = planId == null ? null : planId.toString();
        this.memberId = memberId;
        this.type = type;
        this.occurredAt = Instant.now();
    }

    public WorkoutPlanEvent(String planId, String memberId, String type, Instant occurredAt) {
        this.planId = planId;
        this.memberId = memberId;
        this.type = type;
        this.occurredAt = occurredAt;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}
