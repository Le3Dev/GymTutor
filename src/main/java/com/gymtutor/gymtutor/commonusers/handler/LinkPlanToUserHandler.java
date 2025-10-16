package com.gymtutor.gymtutor.commonusers.handler;// java


import com.gymtutor.gymtutor.commonusers.workoutplanperuser.WorkoutPlanPerUserId;
import com.gymtutor.gymtutor.commonusers.workoutplanperuser.WorkoutPlanPerUserModel;
import com.gymtutor.gymtutor.commonusers.workoutplanperuser.WorkoutPlanPerUserRepository;
import com.gymtutor.gymtutor.user.User;
import com.gymtutor.gymtutor.user.UserService;

public class LinkPlanToUserHandler extends AbstractPlanCopyHandler {

    private final WorkoutPlanPerUserRepository workoutPlanPerUserRepository;
    private final UserService userService;

    public LinkPlanToUserHandler(WorkoutPlanPerUserRepository repo, UserService userService) {
        this.workoutPlanPerUserRepository = repo;
        this.userService = userService;
    }

    @Override
    public void handle(CopyContext ctx) {
        User user = userService.findById(ctx.receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        WorkoutPlanPerUserModel link = new WorkoutPlanPerUserModel();
        WorkoutPlanPerUserId id = new WorkoutPlanPerUserId(ctx.clonedPlan.getWorkoutPlanId(), ctx.receiverId);
        link.setWorkoutPlanPerUserId(id);
        link.setWorkoutPlan(ctx.clonedPlan);
        link.setUser(user);

        workoutPlanPerUserRepository.save(link);

        // store the link in context for next handlers if needed
        callNext(ctx);
    }
}