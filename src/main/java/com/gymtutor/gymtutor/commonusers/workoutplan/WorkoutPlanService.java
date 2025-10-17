package com.gymtutor.gymtutor.commonusers.workoutplan;


import com.gymtutor.gymtutor.commonusers.workout.WorkoutModel;
import com.gymtutor.gymtutor.commonusers.workoutactivities.WorkoutActivitiesModel;
import com.gymtutor.gymtutor.commonusers.workoutexecutionrecordperuser.WorkoutExecutionRecordPerUserModel;
import com.gymtutor.gymtutor.commonusers.workoutexecutionrecordperuser.WorkoutExecutionRecordPerUserService;
import com.gymtutor.gymtutor.commonusers.workoutperworkoutplan.WorkoutPerWorkoutPlanModel;
import com.gymtutor.gymtutor.commonusers.workoutplanperuser.WorkoutPlanPerUserId;
import com.gymtutor.gymtutor.commonusers.workoutplanperuser.WorkoutPlanPerUserModel;
import com.gymtutor.gymtutor.commonusers.workoutplanperuser.WorkoutPlanPerUserRepository;
import com.gymtutor.gymtutor.security.CustomUserDetails;
import com.gymtutor.gymtutor.user.User;
import com.gymtutor.gymtutor.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WorkoutPlanService {

    private static volatile WorkoutPlanService instance;

    private WorkoutPlanRepository workoutPlanRepository;
    private WorkoutExecutionRecordPerUserService workoutExecutionRecordperUserService;
    private WorkoutPlanPerUserRepository workoutPlanPerUserRepository;

    private WorkoutPlanService() {}

    public static WorkoutPlanService getInstance() {
        if (instance == null) {
            synchronized (WorkoutPlanService.class) {
                if (instance == null) {
                    instance = new WorkoutPlanService();
                }
            }
        }
        return instance;
    }


    public void setWorkoutPlanRepository(WorkoutPlanRepository workoutPlanRepository) {
        this.workoutPlanRepository = workoutPlanRepository;
    }

    public void setWorkoutExecutionRecordperUserService(WorkoutExecutionRecordPerUserService workoutExecutionRecordperUserService) {
        this.workoutExecutionRecordperUserService = workoutExecutionRecordperUserService;
    }

    public void setWorkoutPlanPerUserRepository(WorkoutPlanPerUserRepository workoutPlanPerUserRepository) {
        this.workoutPlanPerUserRepository = workoutPlanPerUserRepository;
    }

    public void createWorkoutPlan(WorkoutPlanModel workoutPlanModel, CustomUserDetails loggedUser) {
        User user = loggedUser.getUser();
        workoutPlanModel.setUser(user);
        workoutPlanRepository.save(workoutPlanModel);

        workoutExecutionRecordperUserService.createInitialCompletedStatusForPlanWhenMeStart(workoutPlanModel, user);
        workoutExecutionRecordperUserService.createInitialExecutionsForPlanWhenMeStart(workoutPlanModel, user);
    }

    public WorkoutPlanModel findById(int workoutPlanId) {
        Optional<WorkoutPlanModel> optionalWorkoutPlanModel = workoutPlanRepository.findById(workoutPlanId);
        return optionalWorkoutPlanModel.orElseThrow(() -> new RuntimeException("workoutPlan not found with id " + workoutPlanId));
    }

    public List<WorkoutPlanModel> findAllByCopiedForUserUserId(int userId) {
        return workoutPlanRepository.findAllByCopiedForUserUserId(userId);
    }

    public void updateWorkoutPlan(WorkoutPlanModel workoutPlanModel, int workoutPlanId) {
        Optional<WorkoutPlanModel> existingWorkoutPlanModel = workoutPlanRepository.findById(workoutPlanId);

        if (existingWorkoutPlanModel.isPresent()) {
            WorkoutPlanModel workoutPlan = existingWorkoutPlanModel.get();
            workoutPlan.setWorkoutPlanName(workoutPlanModel.getWorkoutPlanName());
            workoutPlan.setWorkoutPlanDescription(workoutPlanModel.getWorkoutPlanDescription());
            workoutPlan.setTargetDaysToComplete(workoutPlanModel.getTargetDaysToComplete());
            workoutPlanRepository.save(workoutPlan);
        }
    }

    public void deleteWorkoutPlan(int workoutPlanId) {
        workoutPlanRepository.deleteById(workoutPlanId);
    }

    @Transactional
    public void linkUserToPlan(WorkoutPlanModel workoutPlan, User user) {
        WorkoutPlanPerUserId id = new WorkoutPlanPerUserId();
        id.setWorkoutPlanId(workoutPlan.getWorkoutPlanId());
        id.setUserId(user.getUserId());

        Optional<WorkoutPlanPerUserModel> existingLink = workoutPlanPerUserRepository.findByWorkoutPlanPerUserId(id);
        if (existingLink.isEmpty()) {
            WorkoutPlanPerUserModel link = new WorkoutPlanPerUserModel();
            link.setWorkoutPlanPerUserId(id);
            link.setWorkoutPlan(workoutPlan);
            link.setUser(user);
            workoutPlanPerUserRepository.save(link);
        }
    }

    public void saveClone(WorkoutPlanModel workoutPlanModel) {
        workoutPlanRepository.save(workoutPlanModel);
    }

    public List<User> findUsersLinkedToWorkoutPlan(int workoutPlanId) {
        return workoutPlanPerUserRepository
                .findByWorkoutPlanWorkoutPlanId(workoutPlanId)
                .stream()
                .map(WorkoutPlanPerUserModel::getUser)
                .collect(Collectors.toList());
    }

    @Transactional
    public void unlinkUserFromPlan(WorkoutPlanModel workoutPlan, User user) {
        WorkoutPlanPerUserId id = new WorkoutPlanPerUserId();
        id.setWorkoutPlanId(workoutPlan.getWorkoutPlanId());
        id.setUserId(user.getUserId());

        Optional<WorkoutPlanPerUserModel> existingLink = workoutPlanPerUserRepository.findByWorkoutPlanPerUserId(id);
        existingLink.ifPresent(workoutPlanPerUserRepository::delete);
    }

    public void activitiesSort(Set<WorkoutPlanModel> allPlans) {
        for (WorkoutPlanModel plan : allPlans) {
            for (WorkoutPerWorkoutPlanModel workoutPerPlan : plan.getWorkoutPerWorkoutPlans()) {
                WorkoutModel workout = workoutPerPlan.getWorkout();
                if (workout != null && workout.getWorkoutActivities() != null) {
                    workout.setWorkoutActivities(
                            workout.getWorkoutActivities().stream()
                                    .sorted(Comparator.comparingInt(WorkoutActivitiesModel::getSequence))
                                    .collect(Collectors.toList())
                    );
                }
            }
        }
    }
}
