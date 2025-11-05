package com.gymtutor.gymtutor.commonusers.workoutplan;

import com.gymtutor.gymtutor.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WorkoutPlanControllerCreateTest {

    private MockMvc mockMvc;

    @Mock
    private WorkoutPlanService workoutPlanService;

    @InjectMocks
    private WorkoutPlanController workoutPlanController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(workoutPlanController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    @DisplayName("CT01 – Caso Válido - No Limite Inferior")
    void ct01() throws Exception {
        doNothing().when(workoutPlanService).createWorkoutPlan(any(), any(CustomUserDetails.class));

        mockMvc.perform(post("/student/workoutplan")
                        .param("workoutPlanName", "ab")
                        .param("workoutPlanDescription", "abcdefghij")
                        .param("targetDaysToComplete", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/workoutplan"))
                .andExpect(flash().attribute("successMessage", "Ficha de treino criada com sucesso!"));
    }

    @Test
    @DisplayName("CT02 – Caso Válido - No Limite Superior")
    void ct02() throws Exception {
        doNothing().when(workoutPlanService).createWorkoutPlan(any(), any(CustomUserDetails.class));

        String longDescription = "a".repeat(200);

        mockMvc.perform(post("/student/workoutplan")
                        .param("workoutPlanName", "abcdefghij")
                        .param("workoutPlanDescription", longDescription)
                        .param("targetDaysToComplete", "365")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/workoutplan"))
                .andExpect(flash().attribute("successMessage", "Ficha de treino criada com sucesso!"));
    }

    @Test
    @DisplayName("CT03 – Caso Válido – Acima do Limite Inferior")
    void ct03() throws Exception {
        doNothing().when(workoutPlanService).createWorkoutPlan(any(), any(CustomUserDetails.class));

        mockMvc.perform(post("/student/workoutplan")
                        .param("workoutPlanName", "abc")
                        .param("workoutPlanDescription", "abcdefghijk")
                        .param("targetDaysToComplete", "2")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/workoutplan"))
                .andExpect(flash().attribute("successMessage", "Ficha de treino criada com sucesso!"));
    }

    @Test
    @DisplayName("CT04 – Caso Válido - Abaixo do Limite Superior")
    void ct04() throws Exception {
        doNothing().when(workoutPlanService).createWorkoutPlan(any(), any(CustomUserDetails.class));

        String longDescription = "a".repeat(199);

        mockMvc.perform(post("/student/workoutplan")
                        .param("workoutPlanName", "abcdefghi")
                        .param("workoutPlanDescription", longDescription)
                        .param("targetDaysToComplete", "364")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/workoutplan"))
                .andExpect(flash().attribute("successMessage", "Ficha de treino criada com sucesso!"));
    }

    @Test
    @DisplayName("CT05 – Caso Inválido – Nome da Ficha Vazio")
    void ct05() throws Exception {
        mockMvc.perform(post("/student/workoutplan")
                        .param("workoutPlanName", "")
                        .param("workoutPlanDescription", "abcdefghij")
                        .param("targetDaysToComplete", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workoutPlanModel", "workoutPlanName"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.workoutPlanModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Nome não pode estar vazio!"))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT06 – Caso Inválido – Descrição Vazia")
    void ct06() throws Exception {
        mockMvc.perform(post("/student/workoutplan")
                        .param("workoutPlanName", "ab")
                        .param("workoutPlanDescription", "")
                        .param("targetDaysToComplete", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workoutPlanModel", "workoutPlanDescription"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.workoutPlanModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Descrição não pode estar vazia!"))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT07 – Caso Inválido – Dias Vazio")
    void ct07() throws Exception {
        mockMvc.perform(post("/student/workoutplan")
                        .param("workoutPlanName", "ab")
                        .param("workoutPlanDescription", "abcdefghij")
                        .param("targetDaysToComplete", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workoutPlanModel", "targetDaysToComplete"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.workoutPlanModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Dias não pode estar vazio!"))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT08 – Caso Inválido – Nome Abaixo do Limite Inferior")
    void ct08() throws Exception {
        mockMvc.perform(post("/student/workoutplan")
                        .param("workoutPlanName", "a")
                        .param("workoutPlanDescription", "abcdefghij")
                        .param("targetDaysToComplete", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workoutPlanModel", "workoutPlanName"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.workoutPlanModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Este campo deve ter entre 2 e 10 caracteres."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT09 – Caso Inválido – Nome Acima do Limite Superior")
    void ct09() throws Exception {
        mockMvc.perform(post("/student/workoutplan")
                        .param("workoutPlanName", "abcdefghijk")
                        .param("workoutPlanDescription", "abcdefghij")
                        .param("targetDaysToComplete", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workoutPlanModel", "workoutPlanName"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.workoutPlanModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Este campo deve ter entre 2 e 10 caracteres."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT10 – Caso Inválido – Descrição Abaixo do Limite Inferior")
    void ct10() throws Exception {
        mockMvc.perform(post("/student/workoutplan")
                        .param("workoutPlanName", "abc")
                        .param("workoutPlanDescription", "abcdefghi")
                        .param("targetDaysToComplete", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workoutPlanModel", "workoutPlanDescription"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.workoutPlanModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Este campo deve ter entre 10 e 200 caracteres."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT11 – Caso Inválido – Descrição Acima do Limite Superior")
    void ct11() throws Exception {
        String longDescription = "a".repeat(201);
        mockMvc.perform(post("/student/workoutplan")
                        .param("workoutPlanName", "abc")
                        .param("workoutPlanDescription", longDescription)
                        .param("targetDaysToComplete", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workoutPlanModel", "workoutPlanDescription"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.workoutPlanModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Este campo deve ter entre 10 e 200 caracteres."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT12 – Caso Inválido – Dias Abaixo do Limite Inferior")
    void ct12() throws Exception {
        mockMvc.perform(post("/student/workoutplan")
                        .param("workoutPlanName", "abc")
                        .param("workoutPlanDescription", "abcdefghij")
                        .param("targetDaysToComplete", "0")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workoutPlanModel", "targetDaysToComplete"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.workoutPlanModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("O número de dias deve ser maior que 0."))
                                )
                        )
                ));
    }


    @Test
    @DisplayName("CT13 – Caso Inválido – Dias Acima do Limite Superior")
    void ct13() throws Exception {
        mockMvc.perform(post("/student/workoutplan")
                        .param("workoutPlanName", "abc")
                        .param("workoutPlanDescription", "abcdefghij")
                        .param("targetDaysToComplete", "366") // acima do máximo 365
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workoutPlanModel", "targetDaysToComplete"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.workoutPlanModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("O número de dias não pode exceder 365."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT14 – Caso Inválido – Dias com valor decimal")
    void ct14() throws Exception {
        mockMvc.perform(post("/student/workoutplan")
                        .param("workoutPlanName", "abc")
                        .param("workoutPlanDescription", "abcdefghij")
                        .param("targetDaysToComplete", "10.5") // valor decimal
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workoutPlanModel", "targetDaysToComplete"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.workoutPlanModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.containsString("Failed to convert property value")
                                        )
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT15 – Caso Inválido – Dias com valor não numérico")
    void ct15() throws Exception {
        mockMvc.perform(post("/student/workoutplan")
                        .param("workoutPlanName", "abc")
                        .param("workoutPlanDescription", "abcdefghij")
                        .param("targetDaysToComplete", "Dez") // valor não numérico
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workoutPlanModel", "targetDaysToComplete"));
    }

}
