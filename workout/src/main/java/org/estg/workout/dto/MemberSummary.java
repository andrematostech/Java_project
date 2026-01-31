package org.estg.workout.dto;

/**
 * Minimal member data returned by Members service.
 * Only what Workout needs to validate.
 */
public class MemberSummary {

    private String id;      // member id
    private String status;  // ACTIVE / SUSPENDED / ...

    public String getId() { // get id
        return id;
    }

    public void setId(String id) { // set id
        this.id = id;
    }

    public String getStatus() { // get status
        return status;
    }

    public void setStatus(String status) { // set status
        this.status = status;
    }
}
