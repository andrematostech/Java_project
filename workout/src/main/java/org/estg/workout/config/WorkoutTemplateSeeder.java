package org.estg.workout.config;

import java.util.Objects;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import org.estg.workout.data.WorkoutTemplateRepository;
import org.estg.workout.model.WorkoutTemplate;

// Inserts default templates on startup if none exist
@Component
public class WorkoutTemplateSeeder implements CommandLineRunner {

    private final WorkoutTemplateRepository repo;

    public WorkoutTemplateSeeder(WorkoutTemplateRepository repo) {
        this.repo = repo;
    }

    // Seed templates only when database table is empty
    @Override
    public void run(String... args) {
        if (repo.count() > 0)
            return;

        repo.save(Objects.requireNonNull(buildTemplate(
                "Beginner Full Body (Fat Loss)",
                "fat_loss",
                "beginner",
                "unisex",
                "full_body",
                3,
                "{\"days\":[{\"day\":\"Day 1\",\"exercises\":[{\"name\":\"Goblet Squat\",\"sets\":3,\"reps\":\"10-12\"},{\"name\":\"Push-ups\",\"sets\":3,\"reps\":\"8-12\"},{\"name\":\"Row (Cable/Band)\",\"sets\":3,\"reps\":\"10-12\"}]}]}")));

        repo.save(Objects.requireNonNull(buildTemplate(
                "Beginner Upper/Lower (Muscle Gain)",
                "muscle_gain",
                "beginner",
                "unisex",
                "upper_lower",
                4,
                "{\"days\":[{\"day\":\"Upper\",\"exercises\":[{\"name\":\"Bench Press\",\"sets\":3,\"reps\":\"8-10\"},{\"name\":\"Lat Pulldown\",\"sets\":3,\"reps\":\"8-10\"}]},{\"day\":\"Lower\",\"exercises\":[{\"name\":\"Leg Press\",\"sets\":3,\"reps\":\"10-12\"},{\"name\":\"RDL\",\"sets\":3,\"reps\":\"8-10\"}]}]}")));
    }

    // Helper to build a template entity
    private WorkoutTemplate buildTemplate(String name, String goal, String level, String sex,
            String splitType, int daysPerWeek, String exercisesJson) {
        WorkoutTemplate t = new WorkoutTemplate();
        t.setName(name);
        t.setGoal(goal);
        t.setLevel(level);
        t.setSex(sex);
        t.setSplitType(splitType);
        t.setDaysPerWeek(daysPerWeek);
        t.setExercisesJson(exercisesJson);
        return t;
    }
}
