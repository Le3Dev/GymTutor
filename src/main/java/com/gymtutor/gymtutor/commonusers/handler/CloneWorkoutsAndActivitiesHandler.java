package com.gymtutor.gymtutor.commonusers.handler;// java

import com.gymtutor.gymtutor.commonusers.workout.WorkoutModel;
import com.gymtutor.gymtutor.commonusers.workout.WorkoutRepository;
import com.gymtutor.gymtutor.commonusers.workoutactivities.WorkoutActivitiesId;
import com.gymtutor.gymtutor.commonusers.workoutactivities.WorkoutActivitiesModel;
import com.gymtutor.gymtutor.commonusers.workoutactivities.WorkoutActivitiesRepository;
import com.gymtutor.gymtutor.commonusers.workoutperworkoutplan.WorkoutPerWorkoutPlanId;
import com.gymtutor.gymtutor.commonusers.workoutperworkoutplan.WorkoutPerWorkoutPlanModel;
import com.gymtutor.gymtutor.commonusers.workoutperworkoutplan.WorkoutPerWorkoutPlanRepository;

import java.util.List;

public class CloneWorkoutsAndActivitiesHandler extends AbstractPlanCopyHandler {

    private final WorkoutRepository workoutRepository;
    private final WorkoutPerWorkoutPlanRepository workoutPerWorkoutPlanRepository;
    private final WorkoutActivitiesRepository workoutActivitiesRepository;

    public CloneWorkoutsAndActivitiesHandler(WorkoutRepository workoutRepository,
                                             WorkoutPerWorkoutPlanRepository workoutPerWorkoutPlanRepository,
                                             WorkoutActivitiesRepository workoutActivitiesRepository) {
        this.workoutRepository = workoutRepository;
        this.workoutPerWorkoutPlanRepository = workoutPerWorkoutPlanRepository;
        this.workoutActivitiesRepository = workoutActivitiesRepository;
    }

    @Override
    public void handle(CopyContext ctx) {
        List<WorkoutPerWorkoutPlanModel> originalLinks = ctx.originalWorkoutLinks;

        for (WorkoutPerWorkoutPlanModel originalLink : originalLinks) {
            var originalWorkout = originalLink.getWorkout();
            WorkoutModel clonedWorkout = new WorkoutModel();
            clonedWorkout.setWorkoutName(originalWorkout.getWorkoutName());
            clonedWorkout.setRestTime(originalWorkout.getRestTime());
            clonedWorkout.setUser(originalWorkout.getUser());
            clonedWorkout.setReceiverUserId(ctx.receiverId);

            workoutRepository.save(clonedWorkout);
            ctx.originalToClonedWorkoutIds.put(originalWorkout.getWorkoutId(), clonedWorkout.getWorkoutId());

            WorkoutPerWorkoutPlanModel newLink = new WorkoutPerWorkoutPlanModel();
            WorkoutPerWorkoutPlanId newId = new WorkoutPerWorkoutPlanId();
            newId.setWorkoutId(clonedWorkout.getWorkoutId());
            newId.setWorkoutPlanId(ctx.clonedPlan.getWorkoutPlanId());
            newLink.setWorkoutPerWorkoutPlanId(newId);
            newLink.setWorkout(clonedWorkout);
            newLink.setWorkoutPlan(ctx.clonedPlan);

            // clone activities
            var originalActivities = originalWorkout.getWorkoutActivities();
            for (var originalActivity : originalActivities) {
                WorkoutActivitiesModel linkedActivity = new WorkoutActivitiesModel();
                linkedActivity.setWorkoutActivitiesId(
                        new WorkoutActivitiesId(clonedWorkout.getWorkoutId(), originalActivity.getActivity().getActivitiesId())
                );
                linkedActivity.setWorkout(clonedWorkout);
                linkedActivity.setActivity(originalActivity.getActivity());
                linkedActivity.setSequence(originalActivity.getSequence());
                linkedActivity.setReps(originalActivity.getReps());
                workoutActivitiesRepository.save(linkedActivity);
            }

            workoutPerWorkoutPlanRepository.save(newLink);
        }

        callNext(ctx);
    }
}