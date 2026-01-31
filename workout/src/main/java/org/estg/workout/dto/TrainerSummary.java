package org.estg.workout.dto;

/**
 * Minimal trainer data returned by Trainers service.
 * Only what Workout needs to validate.
 */
public class TrainerSummary {

    private String id;      // trainer id
    private String status;  // ACTIVE / INACTIVE / ...

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
