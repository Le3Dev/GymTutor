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
/**
 * Classe de Configuração do Spring (@Configuration).
 *
 * A responsabilidade desta classe é "montar" a cadeia de responsabilidade.
 * Ela injeta todas as dependências (repositórios, serviços) necessárias
 * para cada handler e, em seguida, "amarra" um handler ao outro na ordem correta.
 */
@Configuration
public class PlanCopyHandlersConfig {

    // Injeção de todas as dependências necessárias para TODOS os handlers
    private final WorkoutPlanService workoutPlanService;
    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final WorkoutPerWorkoutPlanRepository workoutPerWorkoutPlanRepository;
    private final WorkoutActivitiesRepository workoutActivitiesRepository;
    private final WorkoutPlanPerUserRepository workoutPlanPerUserRepository;
    private final UserService userService;
    private final WorkoutExecutionRecordPerUserService workoutExecutionRecordPerUserService;

    // Construtor para injeção de dependência
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

    /**
     * Define a cadeia de cópia como um Bean do Spring.
     * Qualquer serviço que precisar "iniciar" o processo de cópia
     * pode simplesmente injetar 'PlanCopyHandler' e receberá 'h1' (o início da cadeia).
     */
    @Bean
    public PlanCopyHandler planCopyChain() {

        // 1. Instancia cada passo (handler) com suas dependências específicas.
        PlanCopyHandler h1 = new ValidateOriginalPlanHandler(workoutPlanService);
        PlanCopyHandler h2 = new ClonePlanHandler(workoutPlanService, userRepository);
        PlanCopyHandler h3 = new CloneWorkoutsAndActivitiesHandler(workoutRepository, workoutPerWorkoutPlanRepository, workoutActivitiesRepository);
        PlanCopyHandler h4 = new LinkPlanToUserHandler(workoutPlanPerUserRepository, userService);
        PlanCopyHandler h5 = new InitializeExecutionRecordsHandler(
                workoutExecutionRecordPerUserService,
                workoutPlanPerUserRepository,
                userRepository
        );

        // 2. Constrói a cadeia (a "linha de montagem") na ordem correta.
        h1.setNext(h2); // Depois de Validar -> Clona o Plano
        h2.setNext(h3); // Depois de Clonar o Plano -> Clona os Workouts
        h3.setNext(h4); // Depois de Clonar os Workouts -> Liga o Plano ao Usuário
        h4.setNext(h5); // Depois de Ligar ao Usuário -> Inicializa os Registros
        // h5 não tem 'next', ele é o fim da cadeia.

        // 3. Retorna o PRIMEIRO handler.
        // Para executar a cadeia, basta chamar h1.handle(ctx).
        return h1;
    }
}