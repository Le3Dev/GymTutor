package com.gymtutor.gymtutor.commonusers.handler;

import com.gymtutor.gymtutor.commonusers.workoutplan.WorkoutPlanModel;
import com.gymtutor.gymtutor.commonusers.workoutplan.WorkoutPlanService;
import com.gymtutor.gymtutor.user.User;
import com.gymtutor.gymtutor.user.UserRepository;

/**
 * PASSO 2 da cadeia.
 *
 * Responsável por criar a cópia "rasa" (shallow copy) do objeto WorkoutPlanModel.
 * Ele cria a "casca" do novo plano, copiando os atributos principais.
 */
public class ClonePlanHandler extends AbstractPlanCopyHandler {

    private final WorkoutPlanService workoutPlanService;
    private final UserRepository userRepository;

    public ClonePlanHandler(WorkoutPlanService workoutPlanService, UserRepository userRepository) {
        this.workoutPlanService = workoutPlanService;
        this.userRepository = userRepository;
    }

    @Override
    public void handle(CopyContext ctx) {
        // 1. Pega o plano original que o handler anterior (Validate) colocou no contexto.
        WorkoutPlanModel original = ctx.originalPlan;

        // 2. Cria uma nova instância para o plano clonado.
        WorkoutPlanModel cloned = new WorkoutPlanModel();

        // 3. Copia as propriedades básicas do original para o clone.
        cloned.setWorkoutPlanName(original.getWorkoutPlanName());
        cloned.setWorkoutPlanDescription(original.getWorkoutPlanDescription());
        cloned.setTargetDaysToComplete(original.getTargetDaysToComplete());
        cloned.setUser(original.getUser()); // Define o *criador* original do plano.

        // 4. Busca o usuário que está *recebendo* a cópia.
        User copyingUser = userRepository.findById(ctx.receiverId).orElseThrow();

        // 5. Define as propriedades específicas da "cópia".
        cloned.setCopiedForUser(copyingUser); // Marca para quem esta cópia se destina.
        cloned.setClonedFrom(original);       // Mantém um "link" para o plano de onde veio.

        // 6. Salva o novo plano "casca" no banco de dados.
        // Isso é importante para que o 'cloned' receba um ID de banco de dados.
        workoutPlanService.saveClone(cloned);

        // 7. Coloca o plano recém-criado (com ID) e o usuário no contexto.
        ctx.clonedPlan = cloned;
        ctx.copyingUser = copyingUser;

        // 8. Passa para o próximo handler (CloneWorkoutsAndActivitiesHandler).
        callNext(ctx);
    }
}