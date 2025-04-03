package com.gymtutor.gymtutor.commonusers.workoutperworkoutplan;

import com.gymtutor.gymtutor.commonusers.workoutplan.WorkoutPlanModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// A anotação @Repository indica que esta interface é um repositório Spring Data JPA,
// que irá gerenciar a entidade WorkoutPlan, permitindo operações CRUD (criação, leitura, atualização, remoção) no banco de dados.
@Repository
public interface WorkoutPerWorkoutPlanRepository extends JpaRepository<WorkoutPerWorkoutPlanModel, Integer> {
}
