package com.gymtutor.gymtutor.commonusers.handler;

import com.gymtutor.gymtutor.commonusers.workout.WorkoutModel;
import com.gymtutor.gymtutor.commonusers.workoutperworkoutplan.WorkoutPerWorkoutPlanModel;
import com.gymtutor.gymtutor.commonusers.workoutplan.WorkoutPlanModel;
import com.gymtutor.gymtutor.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * O objeto "Contexto" (ou "Carga Útil").
 *
 * Este objeto é criado no início da cadeia e passado de handler em handler.
 * Cada handler lê os dados de que precisa deste objeto e/ou
 * escreve seus resultados nele para que os handlers seguintes possam usá-los.
 */
public class CopyContext {

    // --- DADOS DE ENTRADA (Input) ---
    /** O ID do plano original que queremos copiar. */
    public final int workoutPlanId;
    /** O ID do usuário que está enviando (proprietário original). */
    public final int senderId;
    /** O ID do usuário que está recebendo a cópia. */
    public final int receiverId;

    // --- DADOS DE ESTADO/RESULTADO (Preenchidos durante a cadeia) ---

    /** O objeto completo do plano original (carregado pelo ValidateOriginalPlanHandler). */
    public WorkoutPlanModel originalPlan;

    /** O objeto do novo plano clonado (criado pelo ClonePlanHandler). */
    public WorkoutPlanModel clonedPlan;

    /** O objeto do usuário que está recebendo a cópia (carregado pelo ClonePlanHandler ou LinkPlanToUserHandler). */
    public User copyingUser;

    /**
     * Um mapa para rastrear os IDs dos workouts.
     * Ex: O workout original 123 foi clonado como 456. O mapa guarda (123 -> 456).
     * Isso é crucial para recriar as associações corretamente.
     */
    public Map<Integer, Integer> originalToClonedWorkoutIds = new HashMap<>();

    /** Os links entre o plano original e seus workouts (carregado pelo ValidateOriginalPlanHandler). */
    public List<WorkoutPerWorkoutPlanModel> originalWorkoutLinks;

    /**
     * Construtor que inicializa o contexto com os dados de entrada necessários.
     */
    public CopyContext(int workoutPlanId, int senderId, int receiverId) {
        this.workoutPlanId = workoutPlanId;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
}