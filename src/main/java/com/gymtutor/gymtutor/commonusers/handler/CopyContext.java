package com.gymtutor.gymtutor.commonusers.handler;

import com.gymtutor.gymtutor.commonusers.workout.WorkoutModel;
import com.gymtutor.gymtutor.commonusers.workoutperworkoutplan.WorkoutPerWorkoutPlanModel;
import com.gymtutor.gymtutor.commonusers.workoutplan.WorkoutPlanModel;
import com.gymtutor.gymtutor.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CopyContext {
    public final int workoutPlanId;
    public final int senderId;
    public final int receiverId;

    public WorkoutPlanModel originalPlan;
    public WorkoutPlanModel clonedPlan;
    public User copyingUser;
    public Map<Integer, Integer> originalToClonedWorkoutIds = new HashMap<>();
    public List<WorkoutPerWorkoutPlanModel> originalWorkoutLinks;

    public CopyContext(int workoutPlanId, int senderId, int receiverId) {
        this.workoutPlanId = workoutPlanId;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
}