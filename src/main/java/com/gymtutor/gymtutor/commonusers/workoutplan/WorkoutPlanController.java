package com.gymtutor.gymtutor.commonusers.workoutplan;

import com.gymtutor.gymtutor.commonusers.workout.WorkoutModel;
import com.gymtutor.gymtutor.commonusers.workoutactivities.WorkoutActivitiesModel;
import com.gymtutor.gymtutor.commonusers.workoutexecutionrecordperuser.WorkoutExecutionRecordPerUserModel;
import com.gymtutor.gymtutor.commonusers.workoutexecutionrecordperuser.WorkoutExecutionRecordPerUserService;
import com.gymtutor.gymtutor.commonusers.workoutperworkoutplan.WorkoutPerWorkoutPlanModel;
import com.gymtutor.gymtutor.commonusers.workoutplanperuser.WorkoutPlanPerUserService;
import com.gymtutor.gymtutor.security.CustomUserDetails;
import com.gymtutor.gymtutor.security.CustomUserDetailsService;
import com.gymtutor.gymtutor.user.User;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


// Controlador para gerenciar as operações relacionadas a workoutPlan.
@Controller
@RequestMapping("/student/workoutplan")
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService = WorkoutPlanService.getInstance();

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    @Autowired
    private WorkoutExecutionRecordPerUserService workoutExecutionRecordPerUserService;

    @Autowired
    public WorkoutPlanController(
            WorkoutPlanRepository workoutPlanRepository,
            WorkoutExecutionRecordPerUserService workoutExecutionRecordPerUserService
    ) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.workoutExecutionRecordPerUserService = workoutExecutionRecordPerUserService;

        // Injeta manualmente os repositórios e serviços no Singleton
        workoutPlanService.setWorkoutPlanRepository(workoutPlanRepository);
        workoutPlanService.setWorkoutExecutionRecordperUserService(workoutExecutionRecordPerUserService);
    }

    @GetMapping
    public String listWorkoutPlan(
            Model model,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return handleRequest(redirectAttributes, model, null, null, () -> {
            int userId = userDetails.getUser().getUserId();
            List<WorkoutPlanModel> createdPlans = workoutPlanRepository.findByUserUserIdAndCopiedForUserIsNull(userId);
            List<WorkoutPlanModel> copiedPlans = workoutPlanService.findAllByCopiedForUserUserId(userId);

            User user = userDetails.getUser();

            List<WorkoutExecutionRecordPerUserModel> variavel = workoutExecutionRecordPerUserService.findAllRecordByPersonalId(user.getUserId());

            Map<Integer, LocalDateTime> lastExecutionMap = new HashMap<>();

            for (WorkoutExecutionRecordPerUserModel r : variavel) {
                if (r == null) continue;

                var idObj = r.getWorkoutExecutionRecordPerUserId();
                if (idObj == null) continue;

                Integer workoutId = idObj.getWorkoutId();
                if (workoutId == null) continue;

                LocalDateTime lastExec = r.getLastExecutionTime();
                // Você pode decidir se quer ou não pular null no valor:
                if (lastExec == null) continue;

                // Se já tem no mapa, não sobrescreve (comportamento original)
                lastExecutionMap.putIfAbsent(workoutId, lastExec);
            }

            // Junta os dois, sem duplicar
            Set<WorkoutPlanModel> allPlans = new LinkedHashSet<>();
            allPlans.addAll(createdPlans);
            allPlans.addAll(copiedPlans);

            workoutPlanService.activitiesSort(allPlans);



            model.addAttribute("lastExecutionMap", lastExecutionMap);
            model.addAttribute("workoutPlanList", allPlans);
            model.addAttribute("LoggedUserId", userId);
            model.addAttribute("body", "student/workoutplan/list");
            return "/fragments/layout";
        });
    }

    @GetMapping("/new")
    public String newWorkoutPlanForm(Model model){
        model.addAttribute("workoutPlanModel", new WorkoutPlanModel());
        model.addAttribute("body", "/student/workoutplan/new");
        return "/fragments/layout";

    }

    @PostMapping
    public String createWorkoutPlan(
            @Valid @ModelAttribute WorkoutPlanModel workoutPlanModel,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal CustomUserDetails loggedUser

    ){
        if(bindingResult.hasErrors()) {
            return handleValidationErrors(model, "/student/workoutplan/new", workoutPlanModel, bindingResult, null);
        }
        return handleRequest(redirectAttributes, model, "student/workoutplan/new", workoutPlanModel, () ->{
            workoutPlanService.createWorkoutPlan(workoutPlanModel, loggedUser);
            redirectAttributes.addFlashAttribute("successMessage", "Ficha de treino criada com sucesso!");
            return "redirect:/student/workoutplan";

    });}

    @GetMapping("/{workoutPlanId}/edit")
    public String editWorkoutPlanForm(
        @PathVariable int workoutPlanId,
        Model model,
        RedirectAttributes redirectAttributes

    ){
    return handleRequest(redirectAttributes, model, null, null, () ->{
        WorkoutPlanModel workoutPlanModel = workoutPlanService.findById(workoutPlanId);
        model.addAttribute("workoutPlanModel", workoutPlanModel);
        model.addAttribute("body", "/student/workoutplan/edit");
        return "/fragments/layout";

    });}

    @PostMapping("/{workoutPlanId}/edit")
    public String updateWorkoutPlan(
            @PathVariable int workoutPlanId,
            @Valid @ModelAttribute WorkoutPlanModel workoutPlanModel,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal CustomUserDetailsService loggedUser
    ){
        if (bindingResult.hasErrors()){
            return handleValidationErrors(model, "/student/workoutplanId/edit", workoutPlanModel, bindingResult, workoutPlanId);

        }return handleRequest(redirectAttributes, model, "student/workoutplan/edit", workoutPlanModel, () -> {
            workoutPlanService.updateWorkoutPlan(workoutPlanModel, workoutPlanId);
            redirectAttributes.addFlashAttribute("successMessage", "Ficha de treino alterada com sucesso!");
            return "redirect:/student/workoutplan";
        });
    }

    @PostMapping("/delete/{workoutPlanId}")
    public String deleteWorkoutPlan(
            @PathVariable int workoutPlanId,
            RedirectAttributes redirectAttributes
    ){
        try{
            workoutPlanService.deleteWorkoutPlan(workoutPlanId);
            redirectAttributes.addFlashAttribute("successMessage", "Item deletado com sucesso!");
            return "redirect:/student/workoutplan";
        }catch (DataIntegrityViolationException ex) {
            // Se não for possível excluir devido a dependências de outros registros, exibe um erro
            redirectAttributes.addFlashAttribute("errorMessage", "Não é possivel excluir a ficha de treino, pois há registros dependentes.");
            return "redirect:/student/workoutplan";
        }catch(Exception ex){
            // Para outros erros, exibe a mensagem de erro
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/student/workoutplan";
        }
    }


    private String handleValidationErrors(Model model, String view, WorkoutPlanModel workoutPlanModel, BindingResult bindingResult, Integer workoutPlanId) {
        model.addAttribute("errorMessage", "Há erros no formulário!");
        model.addAttribute("org.springframework.validation.BindingResult.WorkoutPlanModel", bindingResult);
        model.addAttribute("WorkoutPlanModel", workoutPlanModel);
        if (workoutPlanId != null) {
            model.addAttribute("workoutPlanId", workoutPlanId);
        }
        model.addAttribute("body", view);

        return "/fragments/layout";
    }


    private String handleRequest(RedirectAttributes redirectAttributes, Model model, String view, WorkoutPlanModel workoutPlanModel, RequestHandler block){
        try{
            return block.execute();
        }catch (Exception ex){
            return handleException(ex, model, workoutPlanModel, view,  redirectAttributes);
        }
    }

    @FunctionalInterface
    public interface RequestHandler{
        String execute();
    }

    public String handleException(Exception ex, Model model, WorkoutPlanModel workoutPlanModel, String view, RedirectAttributes redirectAttributes){
        if(ex instanceof IllegalArgumentException){
            return handleIllegalArgumentException((IllegalArgumentException) ex, model, workoutPlanModel, view);
        } else if (ex instanceof IllegalAccessException) {
            return handleIllegalAccessException((IllegalAccessException) ex, redirectAttributes);
        } else if (ex instanceof DataIntegrityViolationException) {
            return handleDataIntegrityViolationException(model, workoutPlanModel, view);
        } else {
            return handleUnexpectedException(model, workoutPlanModel, view);
        }
    }

    private String handleIllegalArgumentException(IllegalArgumentException ex, Model model, WorkoutPlanModel workoutPlanModel, String view){
        return handleError(ex.getMessage(), model, workoutPlanModel, view);
    }

    private String handleIllegalAccessException(IllegalAccessException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/student/workoutplan";
    }

    private String handleDataIntegrityViolationException(Model model, WorkoutPlanModel workoutPlanModel, String view){
        return handleError("Erro de integridade de dados.", model, workoutPlanModel, view);
    }

    private String handleUnexpectedException(Model model, WorkoutPlanModel workoutPlanModel, String view){
        return handleError("Erro inesperado. Tente novamente.", model, workoutPlanModel, view);

    }

    private String handleError(String errorMessage, Model model, WorkoutPlanModel workoutPlanModel, String view){
        if (model != null) {
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("WorkoutPlanModel", workoutPlanModel);
        }
        if(view != null){
            assert model != null;
            model.addAttribute("body", view);
            return "/fragments/layout";
        }else{
            return "redirect:/student/workoutplan";
        }
    }

}