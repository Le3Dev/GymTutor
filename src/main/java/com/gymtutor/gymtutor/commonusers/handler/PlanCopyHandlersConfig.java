package com.gymtutor.gymtutor.commonusers.handler;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.gymtutor.gymtutor.commonusers.workoutplan.WorkoutPlanService;
import com.gymtutor.gymtutor.commonusers.workout.WorkoutRepository;
import com.gymtutor.gymtutor.commonusers.workoutperworkoutplan.WorkoutPerWorkoutPlanRepository;
import com.gymtutor.gymtutor.commonusers.workoutactivities.WorkoutActivitiesRepository;
import com.gymtutor.gymtutor.commonusers.workoutplanperuser.WorkoutPlanPerUserRepository;
import com.gymtutor.gymtutor.user.UserRepository;
import com.gymtutor.gymtutor.user.UserService;
import com.gymtutor.gymtutor.commonusers.workoutexecutionrecordperuser.WorkoutExecutionRecordPerUserService;

@Configuration
public class PlanCopyHandlersConfig {

    private final WorkoutPlanService workoutPlanService;
    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final WorkoutPerWorkoutPlanRepository workoutPerWorkoutPlanRepository;
    private final WorkoutActivitiesRepository workoutActivitiesRepository;
    private final WorkoutPlanPerUserRepository workoutPlanPerUserRepository;
    private final UserService userService;
    private final WorkoutExecutionRecordPerUserService workoutExecutionRecordPerUserService;

    public PlanCopyHandlersConfig(WorkoutPlanService workoutPlanService,
                                  UserRepository userRepository,
                                  WorkoutRepository workoutRepository,
                                  WorkoutPerWorkoutPlanRepository workoutPerWorkoutPlanRepository,
                                  WorkoutActivitiesRepository workoutActivitiesRepository,
                                  WorkoutPlanPerUserRepository workoutPlanPerUserRepository,
                                  UserService userService,
                                  WorkoutExecutionRecordPerUserService workoutExecutionRecordPerUserService) {
        this.workoutPlanService = workoutPlanService;
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.workoutPerWorkoutPlanRepository = workoutPerWorkoutPlanRepository;
        this.workoutActivitiesRepository = workoutActivitiesRepository;
        this.workoutPlanPerUserRepository = workoutPlanPerUserRepository;
        this.userService = userService;
        this.workoutExecutionRecordPerUserService = workoutExecutionRecordPerUserService;
    }

    @Bean
    public PlanCopyHandler planCopyChain() {
        PlanCopyHandler h1 = new ValidateOriginalPlanHandler(workoutPlanService);
        PlanCopyHandler h2 = new ClonePlanHandler(workoutPlanService, userRepository);
        PlanCopyHandler h3 = new CloneWorkoutsAndActivitiesHandler(workoutRepository, workoutPerWorkoutPlanRepository, workoutActivitiesRepository);
        PlanCopyHandler h4 = new LinkPlanToUserHandler(workoutPlanPerUserRepository, userService);
        PlanCopyHandler h5 = new InitializeExecutionRecordsHandler(
                workoutExecutionRecordPerUserService,
                workoutPlanPerUserRepository,
                userRepository
        );
        h1.setNext(h2);
        h2.setNext(h3);
        h3.setNext(h4);
        h4.setNext(h5);

        return h1;
    }
}