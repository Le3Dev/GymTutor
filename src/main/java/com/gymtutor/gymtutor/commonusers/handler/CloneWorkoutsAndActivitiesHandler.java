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
        // 1. Pega a lista de links (Workout-Plano) originais que o Passo 1 carregou.
        List<WorkoutPerWorkoutPlanModel> originalLinks = ctx.originalWorkoutLinks;

        // 2. Itera sobre cada workout (treino) do plano original.
        for (WorkoutPerWorkoutPlanModel originalLink : originalLinks) {
            var originalWorkout = originalLink.getWorkout();

            // 3. Cria uma nova instância de WorkoutModel (o clone do treino).
            WorkoutModel clonedWorkout = new WorkoutModel();
            clonedWorkout.setWorkoutName(originalWorkout.getWorkoutName());
            clonedWorkout.setRestTime(originalWorkout.getRestTime());
            clonedWorkout.setUser(originalWorkout.getUser()); // Mantém o criador original.
            clonedWorkout.setReceiverUserId(ctx.receiverId);  // Define o novo "dono" do treino.

            // 4. Salva o clone do treino para obter seu novo ID.
            workoutRepository.save(clonedWorkout);

            // 5. Armazena o mapeamento do ID antigo -> ID novo no contexto.
            // (Ex: Treino original 50 foi clonado como 101. Salva 50 -> 101)
            ctx.originalToClonedWorkoutIds.put(originalWorkout.getWorkoutId(), clonedWorkout.getWorkoutId());

            // 6. Cria o *novo link* para associar o *novo treino* ao *novo plano*.
            WorkoutPerWorkoutPlanModel newLink = new WorkoutPerWorkoutPlanModel();
            WorkoutPerWorkoutPlanId newId = new WorkoutPerWorkoutPlanId();
            newId.setWorkoutId(clonedWorkout.getWorkoutId()); // ID do treino clonado
            newId.setWorkoutPlanId(ctx.clonedPlan.getWorkoutPlanId()); // ID do plano clonado
            newLink.setWorkoutPerWorkoutPlanId(newId);
            newLink.setWorkout(clonedWorkout);
            newLink.setWorkoutPlan(ctx.clonedPlan);

            // 7. Agora, clona as atividades (exercícios) dentro deste workout.
            var originalActivities = originalWorkout.getWorkoutActivities();
            for (var originalActivity : originalActivities) {
                // 8. Cria o novo link da atividade.
                WorkoutActivitiesModel linkedActivity = new WorkoutActivitiesModel();

                // 9. Define a chave composta (ID do novo workout + ID da atividade original)
                //    (Assume-se que a 'Activity' em si é uma entidade compartilhada e não precisa ser clonada).
                linkedActivity.setWorkoutActivitiesId(
                        new WorkoutActivitiesId(clonedWorkout.getWorkoutId(), originalActivity.getActivity().getActivitiesId())
                );
                linkedActivity.setWorkout(clonedWorkout); // Associa ao novo workout
                linkedActivity.setActivity(originalActivity.getActivity()); // Reutiliza a atividade

                // 10. Copia as propriedades da associação (ordem, repetições).
                linkedActivity.setSequence(originalActivity.getSequence());
                linkedActivity.setReps(originalActivity.getReps());

                // 11. Salva o link da nova atividade.
                workoutActivitiesRepository.save(linkedActivity);
            }

            // 12. Salva o link principal (Plano <-> Workout).
            workoutPerWorkoutPlanRepository.save(newLink);
        }

        // 13. Passa para o próximo handler (LinkPlanToUserHandler).
        callNext(ctx);
    }
}