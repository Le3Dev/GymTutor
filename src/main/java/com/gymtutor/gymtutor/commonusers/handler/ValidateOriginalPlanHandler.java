package com.gymtutor.gymtutor.commonusers.handler;



import com.gymtutor.gymtutor.commonusers.workoutplan.WorkoutPlanService;

public class ValidateOriginalPlanHandler extends AbstractPlanCopyHandler {

    private final WorkoutPlanService workoutPlanService;

    public ValidateOriginalPlanHandler(WorkoutPlanService workoutPlanService) {
        this.workoutPlanService = workoutPlanService;
    }

    @Override
    public void handle(CopyContext ctx) {
        ctx.originalPlan = workoutPlanService.findById(ctx.workoutPlanId);
        if (ctx.originalPlan == null) {
            throw new IllegalArgumentException("Plano original não encontrado: " + ctx.workoutPlanId);
        }
        ctx.originalWorkoutLinks = ctx.originalPlan.getWorkoutPerWorkoutPlans();
        callNext(ctx);
    }
}
