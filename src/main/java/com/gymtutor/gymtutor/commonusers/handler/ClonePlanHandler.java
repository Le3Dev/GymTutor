package com.gymtutor.gymtutor.commonusers.handler;// java

import com.gymtutor.gymtutor.commonusers.handler.AbstractPlanCopyHandler;
import com.gymtutor.gymtutor.commonusers.workoutplan.WorkoutPlanModel;
import com.gymtutor.gymtutor.commonusers.workoutplan.WorkoutPlanService;
import com.gymtutor.gymtutor.user.User;
import com.gymtutor.gymtutor.user.UserRepository;

public class ClonePlanHandler extends AbstractPlanCopyHandler {

    private final WorkoutPlanService workoutPlanService;
    private final UserRepository userRepository;

    public ClonePlanHandler(WorkoutPlanService workoutPlanService, UserRepository userRepository) {
        this.workoutPlanService = workoutPlanService;
        this.userRepository = userRepository;
    }

    @Override
    public void handle(CopyContext ctx) {
        WorkoutPlanModel original = ctx.originalPlan;
        WorkoutPlanModel cloned = new WorkoutPlanModel();
        cloned.setWorkoutPlanName(original.getWorkoutPlanName());
        cloned.setWorkoutPlanDescription(original.getWorkoutPlanDescription());
        cloned.setTargetDaysToComplete(original.getTargetDaysToComplete());
        cloned.setUser(original.getUser());
        User copyingUser = userRepository.findById(ctx.receiverId).orElseThrow();
        cloned.setCopiedForUser(copyingUser);
        cloned.setClonedFrom(original);

        workoutPlanService.saveClone(cloned);
        ctx.clonedPlan = cloned;
        ctx.copyingUser = copyingUser;
        callNext(ctx);
    }
}