
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
        // 1. Cria o ID composto para encontrar o link criado pelo handler anterior.
        var id = new WorkoutPlanPerUserId();
        id.setWorkoutPlanId(ctx.clonedPlan.getWorkoutPlanId());
        id.setUserId(ctx.receiverId);

        // 2. Busca o link (WorkoutPlanPerUserModel) da base de dados.
        WorkoutPlanPerUserModel link = workoutPlanPerUserRepository.findByWorkoutPlanPerUserId(id)
                .orElseThrow();

        // 3. Garante que a entidade 'User' está "gerenciada" pelo JPA.
        // Isso evita problemas de "detached entity" ao salvar os novos registros.
        User managedUser = userRepository.findById(link.getUser().getUserId())
                .orElse(link.getUser());

        // 4. Chama os serviços para criar os registros de status inicial (Ex: "Não iniciado").
        recordService.createInitialCompletedStatusForPlanWhenMeStart(link.getWorkoutPlan(), managedUser);

        // 5. Chama os serviços para criar os registros de execução (Ex: "Agachamento - 0/10 reps").
        recordService.createInitialExecutionsForPlanWhenMeStart(link.getWorkoutPlan(), managedUser);

        // 6. Chama o próximo (que é nulo), encerrando a cadeia.
        callNext(ctx);
    }
}