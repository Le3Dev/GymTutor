package com.gymtutor.gymtutor.commonusers.workout;


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

class WorkoutControllerEditTest {

    private static final int WORKOUT_ID = 1;

    private MockMvc mockMvc;

    @Mock
    private WorkoutService workoutService;

    @InjectMocks
    private WorkoutController workoutController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(workoutController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    @DisplayName("CT01 – Caso Válido - No Limite Inferior")
    void ct01() throws Exception {
        doNothing().when(workoutService).updateWorkout(any(), any(Integer.class));

        mockMvc.perform(post("/student/workout/{workoutId}/edit", WORKOUT_ID)
                        .param("workoutName", "ab")
                        .param("restTime", "1234567890")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/workout"))
                .andExpect(flash().attribute("successMessage", "Treino alterado com sucesso!"));
    }

    @Test
    @DisplayName("CT02 – Caso Válido - No Limite Superior")
    void ct02() throws Exception {
        String workoutName = "a".repeat(200);
        String restTime = "b".repeat(30);
        doNothing().when(workoutService).updateWorkout(any(), any(Integer.class));

        mockMvc.perform(post("/student/workout/{workoutId}/edit", WORKOUT_ID)
                        .param("workoutName", workoutName)
                        .param("restTime", restTime)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/workout"))
                .andExpect(flash().attribute("successMessage", "Treino alterado com sucesso!"));
    }

    @Test
    @DisplayName("CT03 – Caso Válido – Acima do Limite Inferior")
    void ct03() throws Exception {
        doNothing().when(workoutService).updateWorkout(any(), any(Integer.class));

        mockMvc.perform(post("/student/workout/{workoutId}/edit", WORKOUT_ID)
                        .param("workoutName", "abc")
                        .param("restTime", "12345678901")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/workout"))
                .andExpect(flash().attribute("successMessage", "Treino alterado com sucesso!"));
    }

    @Test
    @DisplayName("CT04 – Caso Válido - Abaixo do Limite Superior")
    void ct04() throws Exception {
        String workoutName = "a".repeat(200);
        String restTime = "b".repeat(29);
        doNothing().when(workoutService).updateWorkout(any(), any(Integer.class));

        mockMvc.perform(post("/student/workout/{workoutId}/edit", WORKOUT_ID)
                        .param("workoutName", workoutName)
                        .param("restTime", restTime)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/workout"))
                .andExpect(flash().attribute("successMessage", "Treino alterado com sucesso!"));
    }

    @Test
    @DisplayName("CT05 – Caso Inválido – Nome Vazio")
    void ct05() throws Exception {
        String restTime = "b".repeat(30);

        mockMvc.perform(post("/student/workout/{workoutId}/edit", WORKOUT_ID)
                        .param("workoutName", "")
                        .param("restTime", restTime)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workoutModel", "workoutName"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.workoutModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Nome não pode estar vazio!"))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT06 – Caso Inválido – Descanso Abaixo do Limite Inferior")
    void ct06() throws Exception {
        mockMvc.perform(post("/student/workout/{workoutId}/edit", WORKOUT_ID)
                        .param("workoutName", "Treino A")
                        .param("restTime", "123456789")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workoutModel", "restTime"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.workoutModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Este campo deve ter entre 10 e 30 caracteres."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT07 – Caso Inválido – Nome Abaixo do Limite Inferior")
    void ct07() throws Exception {
        mockMvc.perform(post("/student/workout/{workoutId}/edit", WORKOUT_ID)
                        .param("workoutName", "a")
                        .param("restTime", "12345678901")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workoutModel", "workoutName"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.workoutModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Este campo deve ter entre 2 e 200 caracteres."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT08 – Caso Inválido – Nome Acima do Limite Superior")
    void ct08() throws Exception {
        String workoutName = "a".repeat(201);

        mockMvc.perform(post("/student/workout/{workoutId}/edit", WORKOUT_ID)
                        .param("workoutName", workoutName)
                        .param("restTime", "12345678901")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workoutModel", "workoutName"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.workoutModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Este campo deve ter entre 2 e 200 caracteres."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT09 – Caso Inválido – Descanso Abaixo do Limite Inferior")
    void ct09() throws Exception {
        mockMvc.perform(post("/student/workout/{workoutId}/edit", WORKOUT_ID)
                        .param("workoutName", "abc")
                        .param("restTime", "123456789")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workoutModel", "restTime"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.workoutModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Este campo deve ter entre 10 e 30 caracteres."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT10 – Caso Inválido – Tempo de Descanso Acima do Limite Superior")
    void ct10() throws Exception {
        String workoutName = "a".repeat(201);
        String restTime = "b".repeat(31);

        mockMvc.perform(post("/student/workout/{workoutId}/edit", WORKOUT_ID)
                        .param("workoutName", workoutName)
                        .param("restTime", restTime)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("workoutModel", "restTime"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.workoutModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Este campo deve ter entre 10 e 30 caracteres."))
                                )
                        )
                ));
    }

}