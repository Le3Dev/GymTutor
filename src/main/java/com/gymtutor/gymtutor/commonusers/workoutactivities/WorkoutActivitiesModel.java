package com.gymtutor.gymtutor.commonusers.workoutactivities;


import com.gymtutor.gymtutor.activities.ActivitiesModel;
import com.gymtutor.gymtutor.commonusers.workout.WorkoutModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_workout_activities")
public class WorkoutActivitiesModel {
    @EmbeddedId
    private WorkoutActivitiesId workoutActivitiesId;

    @ManyToOne
    @JoinColumn(name = "workoutId", nullable = false, updatable = false, insertable = false) // FK para Workout
    private WorkoutModel workout;

    @ManyToOne
    @JoinColumn(name = "activityId", nullable = false, updatable = false, insertable = false) // FK para Activity
    private ActivitiesModel activity;

    @NotBlank(message = "Repetição não pode estar vazio!")
    @Size(min = 1, max=20 , message = "Este campo deve ter entre 1 e 20 caracteres.")
    private String reps; // Número de repetições

    @NotBlank(message = "Sequencia não pode estar vazio!")
    private byte sequence; // Número de sequências

    public WorkoutActivitiesModel() {}

    public WorkoutActivitiesModel(WorkoutModel workout, ActivitiesModel activity, String reps, byte sequence) {
        this.workout = workout;
        this.activity = activity;
        this.reps = reps;
        this.sequence = sequence;
    }

    public String getReps() { return reps; }
    public void setReps(String reps) { this.reps = reps; }

    public byte getSequence() { return sequence; }
    public void setSequence(byte sequence) { this.sequence = sequence; }

    public WorkoutModel getWorkout() { return workout; }
    public void setWorkout(WorkoutModel workout) { this.workout = workout; }

    public ActivitiesModel getActivity() { return activity; }
    public void setActivity(ActivitiesModel activity) { this.activity = activity; }
}

