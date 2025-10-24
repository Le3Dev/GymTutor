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
        // 1. Busca o objeto User completo do usuário receptor.
        User user = userService.findById(ctx.receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // 2. Cria a nova entidade de "link" (Plano por Usuário).
        WorkoutPlanPerUserModel link = new WorkoutPlanPerUserModel();

        // 3. Define a chave composta (ID do plano clonado + ID do usuário receptor).
        WorkoutPlanPerUserId id = new WorkoutPlanPerUserId(ctx.clonedPlan.getWorkoutPlanId(), ctx.receiverId);
        link.setWorkoutPlanPerUserId(id);

        // 4. Define as referências de objeto.
        link.setWorkoutPlan(ctx.clonedPlan);
        link.setUser(user);

        // 5. Salva a associação no banco de dados.
        workoutPlanPerUserRepository.save(link);

        // 6. Passa para o próximo handler (InitializeExecutionRecordsHandler).
        callNext(ctx);
    }
}