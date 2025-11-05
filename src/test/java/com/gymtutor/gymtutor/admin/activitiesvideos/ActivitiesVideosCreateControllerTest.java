package com.gymtutor.gymtutor.admin.activitiesvideos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ActivitiesVideosCreateControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ActivitiesVideosService videosService;

    @InjectMocks
    private ActivitiesVideosController activitiesVideosController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(activitiesVideosController).build();
    }

    @Test
    @DisplayName("CT01 – Caso Valido - No Limite Inferior")
    void ct01() throws Exception {
        // Simula comportamento do service
        doNothing().when(videosService).createVideo(any(), any(Integer.class));

        mockMvc.perform(post("/admin/activities/1/videos/new")
                        .param("videoName", "abc")
                        .param("videoLink", "https://www.youtube.com/watch?v=abc123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/activities/1/videos"))
                .andExpect(flash().attribute("successMessage", "Vídeo adicionado com sucesso!!!"));
    }

    @Test
    @DisplayName("CT02 – Caso Valido – No Limite Superior")
    void ct02() throws Exception {
        doNothing().when(videosService).createVideo(any(), any(Integer.class));

        mockMvc.perform(post("/admin/activities/1/videos/new")
                        .param("videoName", "a".repeat(100))
                        .param("videoLink", "https://youtu.be/xyz789")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/activities/1/videos"))
                .andExpect(flash().attribute("successMessage", "Vídeo adicionado com sucesso!!!"));
    }

    @Test
    @DisplayName("CT03 – Caso Valido – Acima do Limite Inferior")
    void ct03() throws Exception {
        doNothing().when(videosService).createVideo(any(), any(Integer.class));

        mockMvc.perform(post("/admin/activities/1/videos/new")
                        .param("videoName", "abcd")
                        .param("videoLink", "https://www.youtube.com/watch?v=abc123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/activities/1/videos"))
                .andExpect(flash().attribute("successMessage", "Vídeo adicionado com sucesso!!!"));
    }

    @Test
    @DisplayName("CT04 – Caso Valido – Abaixo do Limite Superior")
    void ct04() throws Exception {
        doNothing().when(videosService).createVideo(any(), any(Integer.class));

        mockMvc.perform(post("/admin/activities/1/videos/new")
                        .param("videoName", "a".repeat(99))
                        .param("videoLink", "https://www.youtube.com/watch?v=abc123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/activities/1/videos"))
                .andExpect(flash().attribute("successMessage", "Vídeo adicionado com sucesso!!!"));
    }

    @Test
    @DisplayName("CT05 – Caso Invalido – Nome Vazio")
    void ct05() throws Exception {
        mockMvc.perform(post("/admin/activities/1/videos/new")
                        .param("videoName", "")
                        .param("videoLink", "https://www.youtube.com/watch?v=abc123")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("activitiesVideosModel", "videoName"))
                .andExpect(model().attribute("org.springframework.validation.BindingResult.activitiesVideosModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("O nome do vídeo não pode estar vazio"))
                ))));
    }

    @Test
    @DisplayName("CT06 – Caso Invalido – Link Vazio")
    void ct06() throws Exception {
        mockMvc.perform(post("/admin/activities/1/videos/new")
                        .param("videoName", "abc")
                        .param("videoLink", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("activitiesVideosModel", "videoLink"))
                .andExpect(model().attribute("org.springframework.validation.BindingResult.activitiesVideosModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("O link do vídeo não pode estar vazio!"))
                ))));
    }

    @Test
    @DisplayName("CT07 – Caso Invalido – Nome Abaixo do Limite Inferior")
    void ct07() throws Exception {
        mockMvc.perform(post("/admin/activities/1/videos/new")
                        .param("videoName", "ab")
                        .param("videoLink", "https://www.youtube.com/watch?v=abc123")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("activitiesVideosModel", "videoName"))
                .andExpect(model().attribute("org.springframework.validation.BindingResult.activitiesVideosModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("O nome do vídeo deve ter entre 3 e 100 caracteres."))
                ))));
    }

    @Test
    @DisplayName("CT08 – Caso Invalido – Nome Acima do Limite Superior")
    void ct08() throws Exception {
        mockMvc.perform(post("/admin/activities/1/videos/new")
                        .param("videoName", "a".repeat(101))
                        .param("videoLink", "https://www.youtube.com/watch?v=abc123")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("activitiesVideosModel", "videoName"))
                .andExpect(model().attribute("org.springframework.validation.BindingResult.activitiesVideosModel",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("O nome do vídeo deve ter entre 3 e 100 caracteres."))
                ))));
    }

    @Test
    @DisplayName("CT09 – Caso Invalido – Link não pertencente ao YouTube")
    void ct09() throws Exception {
        doCallRealMethod().when(videosService).createVideo(any(), any(Integer.class));

        mockMvc.perform(post("/admin/activities/1/videos/new")
                        .param("videoName", "abc")
                        .param("videoLink", "http://www.video.com.br")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("activitiesVideosModel"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "O link precisa ser um vídeo do YouTube."));
    }

    @Test
    @DisplayName("CT10 – Caso Invalido – Link inválido")
    void ct10() throws Exception {
        doCallRealMethod().when(videosService).createVideo(any(), any(Integer.class));

        mockMvc.perform(post("/admin/activities/1/videos/new")
                        .param("videoName", "abc")
                        .param("videoLink", "abc")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("activitiesVideosModel"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "O link precisa ser um vídeo do YouTube."));
    }

}