package com.gymtutor.gymtutor.commonusers.workoutexecutionrecordperuser;

import com.gymtutor.gymtutor.commonusers.workout.WorkoutModel;
import com.gymtutor.gymtutor.commonusers.workout.WorkoutService;
import com.gymtutor.gymtutor.commonusers.workoutperworkoutplan.WorkoutPerWorkoutPlanModel;
import com.gymtutor.gymtutor.commonusers.workoutperworkoutplan.WorkoutPerWorkoutPlanRepository;
import com.gymtutor.gymtutor.commonusers.workoutplan.WorkoutPlanModel;
import com.gymtutor.gymtutor.commonusers.workoutplan.WorkoutPlanService;
import com.gymtutor.gymtutor.user.User;
import com.gymtutor.gymtutor.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WorkoutExecutionRecordPerUserService {

    @Autowired
    private WorkoutService workoutService;

    @Autowired
    private WorkoutExecutionRecordPerUserRepository workoutExecutionRecordPerUserRepository;

    @Autowired
    private WorkoutPlanService workoutPlanService;

    @Autowired
    private CompletedWorkoutPlanRepository completedWorkoutPlanRepository;

    @Autowired
    private WorkoutPerWorkoutPlanRepository workoutPerWorkoutPlanRepository;

    @Autowired
    private UserRepository userRepository;

    public void workoutCheck(Integer receiverId, Integer workoutPlanId, Integer workoutId) {
        WorkoutPlanModel workoutPlan = workoutPlanService.findById(workoutPlanId);
        User sender = workoutPlan.getUser();

        Optional<WorkoutExecutionRecordPerUserModel> optionalRecord =
                workoutExecutionRecordPerUserRepository.findRecord(sender.getUserId(), receiverId, workoutPlanId, workoutId);

        if (optionalRecord.isEmpty()) {
            throw new IllegalArgumentException("Não foi possível localizar o treino. Verifique se ele ainda está vinculado à ficha.");
        }
        // checa se ja passou 20 minutos de todos os treinos para poder completar mais um
        if (canMarkWorkoutAsCompleted(workoutPlanId)) {
            WorkoutExecutionRecordPerUserModel record = optionalRecord.get();

            record.setExecutionCount((short) (record.getExecutionCount() + 1));
            // Checa se a soma de treinos concluidos ja completou a ficha de treino
            checkTotalCompletions(receiverId, workoutPlanId);
            record.setLastExecutionTime(LocalDateTime.now());

            workoutExecutionRecordPerUserRepository.save(record);
        } else {
            throw new IllegalStateException("Você só pode concluir um novo treino após 20 minutos do último.");
        }

    }

    @Transactional
    public void createInitialExecutionsForPlanWhenMeStart(WorkoutPlanModel workoutPlan, User user) {
        List<WorkoutModel> treinos = workoutService.findByWorkoutPlanId(workoutPlan.getWorkoutPlanId());

        User managedUser = userRepository.findById(user.getUserId()).orElse(user);

        for (WorkoutModel treino : treinos) {
            WorkoutExecutionRecordPerUserModel execucao = new WorkoutExecutionRecordPerUserModel();

            WorkoutExecutionRecordPerUserId execId = new WorkoutExecutionRecordPerUserId();
            execId.setSenderId(managedUser.getUserId());
            execId.setReceiverId(managedUser.getUserId()); // eu mesmo iniciei entao eu sou o cara que manda e recebe
            execId.setWorkoutPlanId(workoutPlan.getWorkoutPlanId());
            execId.setWorkoutId(treino.getWorkoutId());

            execucao.setWorkoutExecutionRecordPerUserId(execId);
            execucao.setUser(managedUser);
            execucao.setExecutionCount((short) 0);
            execucao.setLastExecutionTime(null);

            workoutExecutionRecordPerUserRepository.save(execucao);
        }
    }

    public void createInitialCompletedStatusForPlanWhenMeStart(WorkoutPlanModel workoutPlan, User user) {
        CompletedWorkoutPlanId completedId = new CompletedWorkoutPlanId();
        completedId.setUserId(user.getUserId());
        completedId.setWorkoutPlanId(workoutPlan.getWorkoutPlanId());

        CompletedWorkoutPlanModel status = new CompletedWorkoutPlanModel();
        status.setCompletedWorkoutPlanId(completedId);
        status.setCompleted(false);
        status.setCompletedAt(null); // ou pode omitir se quiser deixar null por padrão

        completedWorkoutPlanRepository.save(status);
    }

    @Transactional
    public void syncExecutionRecordsWithWorkoutPlan(WorkoutPlanModel workoutPlan, User receiver) {
        List<WorkoutModel> workoutsInPlan = workoutPerWorkoutPlanRepository.findAllWorkoutsByWorkoutPlan(workoutPlan);
        List<WorkoutExecutionRecordPerUserModel> existingRecords =
                workoutExecutionRecordPerUserRepository.findAllByWorkoutPlanId(workoutPlan.getWorkoutPlanId());

        Set<Integer> existingWorkoutIds = existingRecords.stream()
                .map(record -> record.getWorkoutExecutionRecordPerUserId().getWorkoutId())
                .collect(Collectors.toSet());

        User managedReceiver = userRepository.findById(receiver.getUserId()).orElse(receiver);

        for (WorkoutModel workout : workoutsInPlan) {
            if (!existingWorkoutIds.contains(workout.getWorkoutId())) {

                WorkoutExecutionRecordPerUserId id = new WorkoutExecutionRecordPerUserId();
                id.setWorkoutId(workout.getWorkoutId());
                id.setReceiverId(managedReceiver.getUserId()); // o aluno
                id.setSenderId(workoutPlan.getUser().getUserId()); // o criador da ficha
                id.setWorkoutPlanId(workoutPlan.getWorkoutPlanId());

                WorkoutExecutionRecordPerUserModel newRecord = new WorkoutExecutionRecordPerUserModel();
                newRecord.setWorkoutExecutionRecordPerUserId(id);
                newRecord.setUser(managedReceiver);
                newRecord.setExecutionCount((short) 0);
                newRecord.setLastExecutionTime(null);

                workoutExecutionRecordPerUserRepository.save(newRecord);
            }
        }
    }

    public void checkTotalCompletions(Integer userId, Integer workoutPlanId) {
        WorkoutPlanModel workoutPlan = workoutPlanService.findById(workoutPlanId);
        List<WorkoutPerWorkoutPlanModel> workouts = workoutPlan.getWorkoutPerWorkoutPlans();

        // Buscar execuções do usuário nessa ficha
        List<WorkoutExecutionRecordPerUserModel> execucoes =
                workoutExecutionRecordPerUserRepository
                        .findAllByWorkoutExecutionRecordPerUserId_ReceiverIdAndWorkoutExecutionRecordPerUserId_WorkoutPlanId(userId, workoutPlanId);

        // Criar um mapa para facilitar o acesso por workoutId
        Map<Integer, Short> execucoesPorWorkout = execucoes.stream()
                .collect(Collectors.toMap(
                        e -> e.getWorkoutExecutionRecordPerUserId().getWorkoutId(),
                        WorkoutExecutionRecordPerUserModel::getExecutionCount
                ));

        int somaTotal = 0;

        for (WorkoutPerWorkoutPlanModel w : workouts) {
            Integer workoutId = w.getWorkout().getWorkoutId();
            Short vezesCompletas = execucoesPorWorkout.getOrDefault(workoutId, (short) 0);

            somaTotal += vezesCompletas;
        }

        if (somaTotal >= workoutPlan.getTargetDaysToComplete()) {
            // notifyPersonalCompletion(userId, workoutPlanId); // todo: Função que avisa o personal que a ficha esta completa
            completeWorkoutPlan(userId, workoutPlanId);
        }
    }

    public void completeWorkoutPlan(Integer userId, Integer workoutPlanId) {
        CompletedWorkoutPlanId id = new CompletedWorkoutPlanId();
        id.setUserId(userId);
        id.setWorkoutPlanId(workoutPlanId);

        CompletedWorkoutPlanModel completedWorkout = completedWorkoutPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de completude não encontrado"));

        completedWorkout.setCompleted(true);
        completedWorkout.setCompletedAt(LocalDateTime.now());
        completedWorkoutPlanRepository.save(completedWorkout);
    }

    public boolean canMarkWorkoutAsCompleted(Integer workoutPlanId) {
        List<WorkoutExecutionRecordPerUserModel> records =
                workoutExecutionRecordPerUserRepository.findAllByWorkoutPlanId(workoutPlanId);

        LocalDateTime agora = LocalDateTime.now();

        for (WorkoutExecutionRecordPerUserModel record : records) {
            LocalDateTime completedAt = record.getLastExecutionTime();

            if (completedAt != null) {
                Duration tempoDesdeUltimaConclusao = Duration.between(completedAt, agora);
                if (tempoDesdeUltimaConclusao.toMinutes() < 20) {
                    return false; // Ainda não passou 20 minutos para pelo menos um treino da ficha
                }
            }
        }
        return true; // Todos estão com mais de 20 minutos ou não foram concluídos ainda
    }

    public void resetAllExecutions(Integer workoutPlanId) {
        List<WorkoutExecutionRecordPerUserModel> records =
                workoutExecutionRecordPerUserRepository.findAllByWorkoutPlanId(workoutPlanId);
        for (WorkoutExecutionRecordPerUserModel record : records) {
            record.setExecutionCount((short) 0);
            workoutExecutionRecordPerUserRepository.save(record);
        }
    }

    public List<WorkoutExecutionRecordPerUserModel> findAllRecordByPersonalId(int personalId) {
        return workoutExecutionRecordPerUserRepository.findAllByWorkoutExecutionRecordPerUserId_SenderId(personalId);
    }

}