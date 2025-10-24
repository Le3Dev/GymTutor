package com.gymtutor.gymtutor.commonusers.handler;

import com.gymtutor.gymtutor.commonusers.workoutplan.WorkoutPlanService;

/**
 * PASSO 1 da cadeia.
 *
 * Responsável por validar se o plano de treino original existe e
 * carregar os dados iniciais (o próprio plano e seus links de workout)
 * no contexto para os próximos handlers usarem.
 */
public class ValidateOriginalPlanHandler extends AbstractPlanCopyHandler {

    private final WorkoutPlanService workoutPlanService;

    public ValidateOriginalPlanHandler(WorkoutPlanService workoutPlanService) {
        this.workoutPlanService = workoutPlanService;
    }

    @Override
    public void handle(CopyContext ctx) {
        // 1. Busca o plano original no banco de dados usando o ID do contexto.
        ctx.originalPlan = workoutPlanService.findById(ctx.workoutPlanId);

        // 2. Se não existir, lança uma exceção. A cadeia para aqui.
        if (ctx.originalPlan == null) {
            throw new IllegalArgumentException("Plano original não encontrado: " + ctx.workoutPlanId);
        }

        // 3. Pré-carrega os links de workout do plano original no contexto.
        // Isso otimiza o processo, pois o próximo handler (CloneWorkouts) precisará deles.
        ctx.originalWorkoutLinks = ctx.originalPlan.getWorkoutPerWorkoutPlans();

        // 4. Se tudo estiver OK, passa para o próximo handler (ClonePlanHandler).
        callNext(ctx);
    }
}