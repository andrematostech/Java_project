package org.estg.workout.model;

public class Exercise {

    private String name;
    private int sets;
    private int reps;
    private int restSeconds;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public int getRestSeconds() { return restSeconds; }
    public void setRestSeconds(int restSeconds) { this.restSeconds = restSeconds; }
}
