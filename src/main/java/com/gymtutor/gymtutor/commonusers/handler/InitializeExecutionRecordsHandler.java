
package com.gymtutor.gymtutor.commonusers.handler;

import com.gymtutor.gymtutor.commonusers.workoutplanperuser.WorkoutPlanPerUserId;
import com.gymtutor.gymtutor.commonusers.workoutplanperuser.WorkoutPlanPerUserModel;
import com.gymtutor.gymtutor.commonusers.workoutexecutionrecordperuser.WorkoutExecutionRecordPerUserService;
import com.gymtutor.gymtutor.commonusers.workoutplanperuser.WorkoutPlanPerUserRepository;
import com.gymtutor.gymtutor.user.User;
import com.gymtutor.gymtutor.user.UserRepository;

public class InitializeExecutionRecordsHandler extends AbstractPlanCopyHandler {

    private final WorkoutExecutionRecordPerUserService recordService;
    private final WorkoutPlanPerUserRepository workoutPlanPerUserRepository;
    private final UserRepository userRepository;

    public InitializeExecutionRecordsHandler(WorkoutExecutionRecordPerUserService recordService,
                                             WorkoutPlanPerUserRepository workoutPlanPerUserRepository,
                                             UserRepository userRepository) {
        this.recordService = recordService;
        this.workoutPlanPerUserRepository = workoutPlanPerUserRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void handle(CopyContext ctx) {
        var id = new WorkoutPlanPerUserId();
        id.setWorkoutPlanId(ctx.clonedPlan.getWorkoutPlanId());
        id.setUserId(ctx.receiverId);

        WorkoutPlanPerUserModel link = workoutPlanPerUserRepository.findByWorkoutPlanPerUserId(id)
                .orElseThrow();

        // garante que o usuário está gerenciado/persistido — evita user_id nulo no INSERT
        User managedUser = userRepository.findById(link.getUser().getUserId())
                .orElse(link.getUser());

        recordService.createInitialCompletedStatusForPlanWhenMeStart(link.getWorkoutPlan(), managedUser);
        recordService.createInitialExecutionsForPlanWhenMeStart(link.getWorkoutPlan(), managedUser);

        callNext(ctx);
    }
}