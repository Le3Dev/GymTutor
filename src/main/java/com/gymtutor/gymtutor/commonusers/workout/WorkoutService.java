package com.gymtutor.gymtutor.commonusers.workout;


import com.gymtutor.gymtutor.commonusers.workoutperworkoutplan.WorkoutPerWorkoutPlanModel;
import com.gymtutor.gymtutor.commonusers.workoutperworkoutplan.WorkoutPerWorkoutPlanRepository;
import com.gymtutor.gymtutor.security.CustomUserDetails;
import com.gymtutor.gymtutor.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Serviço para gerenciar a lógica de negócios relacionada a entidades workout (WorkoutService),
// incluindo operações de criação, atualização, exclusão e recuperação de entidades,
// além de realizar validações de permissão de acesso para o usuário logado.
@Service
public class WorkoutService {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private WorkoutPerWorkoutPlanRepository workoutPerWorkoutPlanRepository;

    public void createWorkout(WorkoutModel workoutModel, CustomUserDetails loggedUser) {
        User user = loggedUser.getUser();
        workoutModel.setUser(user);
        workoutModel.setReceiverUserId(user.getUserId());
        workoutRepository.save(workoutModel);}

    public WorkoutModel findById(int workoutId){
        Optional<WorkoutModel> optionalWorkoutModel = workoutRepository.findById(workoutId);
        return optionalWorkoutModel.orElseThrow(() -> new RuntimeException("workout not found with id" + workoutId));
    }

    public List<WorkoutModel> findAllByReceiverUserId(int userId) {
        return workoutRepository.findAllByReceiverUserId(userId);
    }

    public void updateWorkout(WorkoutModel workoutModel, int workoutId){

        // Busca a atividade existente pelo ID
        Optional<WorkoutModel> existingWorkout = workoutRepository.findById(workoutId);

        // Se a atividade for encontrada, atualiza os dados
        if (existingWorkout.isPresent()){
            WorkoutModel workout = existingWorkout.get();

            // Atualiza os campos da atividade
            workout.setWorkoutActivities(workoutModel.getWorkoutActivities());
            workout.setWorkoutName(workoutModel.getWorkoutName());
            workout.setRestTime(workoutModel.getRestTime());

            // Salva o treino atualizada no banco de dados
            workoutRepository.save(workout);

        }
    }

    public void deleteWorkout(int workoutId){
        workoutRepository.deleteById(workoutId);
    }

    public List<WorkoutModel> findByWorkoutPlanId(int workoutPlanId) {
        List<WorkoutPerWorkoutPlanModel> relacoes =
                workoutPerWorkoutPlanRepository.findByWorkoutPlan_WorkoutPlanId(workoutPlanId);

        return relacoes.stream()
                .map(WorkoutPerWorkoutPlanModel::getWorkout)
                .collect(Collectors.toList());
    }

}
