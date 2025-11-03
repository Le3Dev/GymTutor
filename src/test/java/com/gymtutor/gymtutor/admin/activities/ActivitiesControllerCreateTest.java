package com.gymtutor.gymtutor.admin.activities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ActivitiesControllerCreateTest {

    private MockMvc mockMvc;

    @Mock
    private ActivitiesService activitiesService;

    @InjectMocks
    private ActivitiesController activitiesController;

    @Mock
    private MuscularGroupRepository muscularGroupRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        when(muscularGroupRepository.findAll()).thenReturn(List.of(
                new MuscularGroupModel() {{ setMuscularGroup(MuscularGroup.ABDOMEN); }},
                new MuscularGroupModel() {{ setMuscularGroup(MuscularGroup.BICEPS); }}
        ));

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(activitiesController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    @DisplayName("CT01 – Caso Valido - No Limite Inferior")
    void ct01() throws Exception {
        doNothing().when(activitiesService).createActivity(any());

        mockMvc.perform(post("/admin/activities")
                        .param("activityName", "abc")
                        .param("activityDescription", "abcdefghij")
                        .param("muscularGroup.muscularGroup", "ABDOMEN")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/activities"))
                .andExpect(flash().attribute("successMessage", "Atividade criada com sucesso!!!"));
    }

    @Test
    @DisplayName("CT02 – Caso Valido - No Limite Superior")
    void ct02() throws Exception {
        doNothing().when(activitiesService).createActivity(any());

        mockMvc.perform(post("/admin/activities")
                        .param("activityName", "a".repeat(100))
                        .param("activityDescription", "b".repeat(200))
                        .param("muscularGroup.muscularGroup", "ABDOMEN")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/activities"))
                .andExpect(flash().attribute("successMessage", "Atividade criada com sucesso!!!"));
    }

    @Test
    @DisplayName("CT03 – Caso Valido – Acima do Limite Inferior")
    void ct03() throws Exception {
        doNothing().when(activitiesService).createActivity(any());

        mockMvc.perform(post("/admin/activities")
                        .param("activityName", "abcd")
                        .param("activityDescription", "abcdefghijk")
                        .param("muscularGroup.muscularGroup", "ABDOMEN")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/activities"))
                .andExpect(flash().attribute("successMessage", "Atividade criada com sucesso!!!"));
    }

    @Test
    @DisplayName("CT04 – Caso Valido - Abaixo do Limite Superior")
    void ct04() throws Exception {
        doNothing().when(activitiesService).createActivity(any());

        mockMvc.perform(post("/admin/activities")
                        .param("activityName", "a".repeat(99))
                        .param("activityDescription", "b".repeat(199))
                        .param("muscularGroup.muscularGroup", "ABDOMEN")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/activities"))
                .andExpect(flash().attribute("successMessage", "Atividade criada com sucesso!!!"));
    }

    @Test
    @DisplayName("CT05 – Caso Invalido – Nome Vazio")
    void ct05() throws Exception {
        mockMvc.perform(post("/admin/activities")
                        .param("activityName", "")
                        .param("activityDescription", "abcdefghij")
                        .param("muscularGroup.muscularGroup", "ABDOMEN")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("activitiesModel", "activityName"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.activitiesModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Nome não pode estar vazio!"))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT06 – Caso Invalido – Nome Abaixo do Limite Inferior")
    void ct06() throws Exception {
        mockMvc.perform(post("/admin/activities")
                        .param("activityName", "ab")
                        .param("activityDescription", "abcdefghij")
                        .param("muscularGroup.muscularGroup", "ABDOMEN")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("activitiesModel", "activityName"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.activitiesModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Nome deve ter entre 3 e 100 caracteres."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT07 – Caso Invalido – Nome Acima do Limite Superior")
    void ct07() throws Exception {
        mockMvc.perform(post("/admin/activities")
                        .param("activityName", "a".repeat(101))
                        .param("activityDescription", "abcdefghij")
                        .param("muscularGroup.muscularGroup", "ABDOMEN")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("activitiesModel", "activityName"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.activitiesModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Nome deve ter entre 3 e 100 caracteres."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT08 – Caso Invalido – Descrição Vazia")
    void ct08() throws Exception {
        mockMvc.perform(post("/admin/activities")
                        .param("activityName", "abc")
                        .param("activityDescription", "")
                        .param("muscularGroup.muscularGroup", "ABDOMEN")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("activitiesModel", "activityDescription"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.activitiesModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Descrição não pode estar vazio!"))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT09 – Caso Invalido – Descrição Abaixo do Limite Inferior")
    void ct09() throws Exception {
        mockMvc.perform(post("/admin/activities")
                        .param("activityName", "abc")
                        .param("activityDescription", "abcdefghi") // 9 chars < 10
                        .param("muscularGroup.muscularGroup", "ABDOMEN")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("activitiesModel", "activityDescription"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.activitiesModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Descrição deve ter entre 10 e 200 caracteres."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("CT10 – Caso Invalido – Descrição Acima do Limite Superior")
    void ct10() throws Exception {
        mockMvc.perform(post("/admin/activities")
                        .param("activityName", "abc")
                        .param("activityDescription", "b".repeat(201)) // 201 chars > 200
                        .param("muscularGroup.muscularGroup", "ABDOMEN")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("activitiesModel", "activityDescription"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.activitiesModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Descrição deve ter entre 10 e 200 caracteres."))
                                )
                        )
                ));
    }


}
