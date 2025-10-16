package com.gymtutor.gymtutor.commonusers.workoutplanperuser;

import com.gymtutor.gymtutor.commonusers.handler.CopyContext;
import com.gymtutor.gymtutor.commonusers.handler.PlanCopyHandler;
import com.gymtutor.gymtutor.commonusers.workoutplan.WorkoutPlanModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class WorkoutPlanPerUserService {

    @Autowired
    private WorkoutPlanPerUserRepository workoutPlanPerUserRepository;


    private final PlanCopyHandler planCopyChain;

    public WorkoutPlanPerUserService(PlanCopyHandler planCopyChain) {
        this.planCopyChain = planCopyChain;
    }

    // delega para a cadeia de handlers
    public void linkUserToPlan(int workoutPlanId, int senderId, int receiverId) {
        CopyContext ctx = new CopyContext(workoutPlanId, senderId, receiverId);
        planCopyChain.handle(ctx);
    }

    // Desvincular treino da ficha de treino
    @Transactional
    public void unlinkWorkoutPlanPerUser(int workoutPlanId, int userId) {
        var id = new WorkoutPlanPerUserId();
        id.setWorkoutPlanId(workoutPlanId);
        id.setUserId(userId);

        Optional<WorkoutPlanPerUserModel> existingLink = workoutPlanPerUserRepository.findByWorkoutPlanPerUserId(id);

        existingLink.ifPresent(workoutPlanPerUserRepository::delete);
    }

    public WorkoutPlanPerUserModel findById(int WorkoutPlanPerUserId){
        Optional<WorkoutPlanPerUserModel> optionalWorkoutPlanPerUserModel = workoutPlanPerUserRepository.findById(WorkoutPlanPerUserId);
        return optionalWorkoutPlanPerUserModel.orElseThrow(() -> new RuntimeException("workoutPlanPerUser not found with id " + WorkoutPlanPerUserId));
    }

    public List<WorkoutPlanPerUserModel> findAllByWorkoutPlanId(int workoutPlanId) {
        return workoutPlanPerUserRepository.findByWorkoutPlanWorkoutPlanId(workoutPlanId);
    }

    public List<WorkoutPlanModel> findWorkoutPlansByUserId(int userId) {
        return workoutPlanPerUserRepository.findWorkoutPlansByUserId(userId);
    }

}
