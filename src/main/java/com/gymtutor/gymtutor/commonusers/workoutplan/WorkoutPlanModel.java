package com.gymtutor.gymtutor.commonusers.workoutplan;

import com.gymtutor.gymtutor.commonusers.workout.WorkoutModel;
import com.gymtutor.gymtutor.commonusers.workoutperworkoutplan.WorkoutPerWorkoutPlanModel;
import com.gymtutor.gymtutor.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Arrays;
import java.util.List;

@Entity
@Table(name="tb_workoutPlan")
public class WorkoutPlanModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int workoutPlanId;

    @NotBlank(message = "Nome não pode estar vazio!")
    @Size(min = 2, max=10 , message = "Este campo deve ter entre 2 e 10 caracteres.")
    private String workoutPlanName;

    @NotBlank(message = "Descrição não pode estar vazia!")
    @Size(min = 10, max=200 , message = "Este campo deve ter entre 10 e 200 caracteres.")
    private String workoutPlanDescription;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "workoutPlan")
    private List<WorkoutPerWorkoutPlanModel> workoutPerWorkoutPlans;

    // Pois na tabela de relacao a gnt ja consegue ver de onde vem o clone do swgundo campo apenas nn aqui na propria model
    @ManyToOne
    @JoinColumn(name = "cloned_from_workout_id")
    private WorkoutPlanModel clonedFrom;

    @ManyToOne
    @JoinColumn(name = "copied_for_user_id")
    private User copiedForUser;

    @Column(name = "target_days_to_complete", nullable = false)
    @Min(value = 1, message = "O número de dias deve ser maior que 0.")
    @Max(value = 365, message = "O número de dias não pode exceder 365.")
    private Short targetDaysToComplete;

    // Construtor padrão
    public WorkoutPlanModel() {
    }

    // Construtor com argumentos
    public WorkoutPlanModel(User user, String workoutPlanName, String workoutPlanDescription, List<WorkoutModel> workoutModel) {
        this.user = user;
        this.workoutPlanName = workoutPlanName;
        this.workoutPlanDescription = workoutPlanDescription;
    }

    // Getters e setters
    public String getWorkoutPlanDescription() {
        return workoutPlanDescription;
    }

    public void setWorkoutPlanDescription(String workoutPlanDescription) {
        this.workoutPlanDescription = workoutPlanDescription;
    }

    public int getWorkoutPlanId() {
        return workoutPlanId;
    }

    public String getWorkoutPlanName() {
        return workoutPlanName;
    }

    public void setWorkoutPlanName(String workoutPlanName) {
        this.workoutPlanName = workoutPlanName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<WorkoutPerWorkoutPlanModel> getWorkoutPerWorkoutPlans() {
        return workoutPerWorkoutPlans;
    }

    public void setWorkoutPerWorkoutPlans(List<WorkoutPerWorkoutPlanModel> workoutPerWorkoutPlans) {
        this.workoutPerWorkoutPlans = workoutPerWorkoutPlans;
    }

    public WorkoutPlanModel getClonedFrom() {
        return clonedFrom;
    }

    public void setClonedFrom(WorkoutPlanModel clonedFrom) {
        this.clonedFrom = clonedFrom;
    }

    public User getCopiedForUser() {
        return copiedForUser;
    }

    public void setCopiedForUser(User copiedForUser) {
        this.copiedForUser = copiedForUser;
    }

    public Short getTargetDaysToComplete() {
        return targetDaysToComplete;
    }

    public void setTargetDaysToComplete(Short targetDaysToComplete) {
        this.targetDaysToComplete = targetDaysToComplete;
    }
}
