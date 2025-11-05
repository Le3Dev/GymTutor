package com.gymtutor.gymtutor.security;

import com.gymtutor.gymtutor.security.facade.AuthFacade;
import com.gymtutor.gymtutor.user.UserRegistrationDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("removal")
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthFacade authFacade;

    @Test
    @DisplayName("Caso de Teste CT01 – Caso Valido - No Limite Inferior")
    void ct01() throws Exception {

        Mockito.doNothing().when(authFacade).registrarUsuario(any(UserRegistrationDTO.class));

        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abc")
                        .param("userEmail", "a@a")
                        .param("confirmEmail", "a@a")
                        .param("userPassword", "12345")
                        .param("confirmPassword", "12345")
                        .param("userCpf", "056.491.310-31"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("successMessage", "Conta criada com sucesso!!!"));
    }

    @Test
    @DisplayName("Caso de Teste CT02 – Caso Valido – Acima do Limite Inferior")
    void ct02() throws Exception {

        Mockito.doNothing().when(authFacade).registrarUsuario(any(UserRegistrationDTO.class));

        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abcd")
                        .param("userEmail", "a@a")
                        .param("confirmEmail", "a@a")
                        .param("userPassword", "123456")
                        .param("confirmPassword", "123456")
                        .param("userCpf", "056.491.310-31"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("successMessage", "Conta criada com sucesso!!!"));
    }

    @Test
    @DisplayName("Caso de Teste CT03 – Caso Inválido – Nome Vazio")
    void ct03() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "")
                        .param("userEmail", "a@a")
                        .param("confirmEmail", "a@a")
                        .param("userPassword", "123456")
                        .param("confirmPassword", "123456")
                        .param("userCpf", "056.491.310-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "userName"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"));
    }

    @Test
    @DisplayName("Caso de Teste CT04 – Caso Inválido – Email Vazio")
    void ct04() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abcd")
                        .param("userEmail", "")
                        .param("confirmEmail", "a@a")
                        .param("userPassword", "123456")
                        .param("confirmPassword", "123456")
                        .param("userCpf", "056.491.310-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "userEmail"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"));
    }

    @Test
    @DisplayName("Caso de Teste CT05 – Caso Inválido – Confirmação de Email Vazio")
    void ct05() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abcd")
                        .param("userEmail", "a@a")
                        .param("confirmEmail", "")
                        .param("userPassword", "123456")
                        .param("confirmPassword", "123456")
                        .param("userCpf", "056.491.310-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "confirmEmail"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"));
    }

    @Test
    @DisplayName("Caso de Teste CT06 – Caso Inválido – Senha Vazio")
    void ct06() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abcd")
                        .param("userEmail", "a@a")
                        .param("confirmEmail", "a@a")
                        .param("userPassword", "")
                        .param("confirmPassword", "123456")
                        .param("userCpf", "056.491.310-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "userPassword"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"));
    }

    @Test
    @DisplayName("Caso de Teste CT07 – Caso Inválido – Confirmação de Senha Vazio")
    void ct07() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abcd")
                        .param("userEmail", "a@a")
                        .param("confirmEmail", "a@a")
                        .param("userPassword", "123456")
                        .param("confirmPassword", "")
                        .param("userCpf", "056.491.310-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "confirmPassword"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"));
    }

    @Test
    @DisplayName("Caso de Teste CT08 – Caso Inválido – CPF Vazio")
    void ct08() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abcd")
                        .param("userEmail", "a@a")
                        .param("confirmEmail", "a@a")
                        .param("userPassword", "123456")
                        .param("confirmPassword", "123456")
                        .param("userCpf", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "userCpf"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"));
    }

    @Test
    @DisplayName("Caso de Teste CT09 – Nome Abaixo do Limite Inferior")
    void ct09() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "ab")
                        .param("userEmail", "a@a")
                        .param("confirmEmail", "a@a")
                        .param("userPassword", "123456")
                        .param("confirmPassword", "123456")
                        .param("userCpf", "056.491.310-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "userName"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "userName"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.userRegistrationDTO",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("O nome deve ter pelo menos 3 caracteres."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("Caso de Teste CT10 – Email sem @")
    void ct10() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abc")
                        .param("userEmail", "aa")
                        .param("confirmEmail", "aa")
                        .param("userPassword", "123456")
                        .param("confirmPassword", "123456")
                        .param("userCpf", "056.491.310-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "userEmail"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "confirmEmail"))
                .andExpect(model().attributeHasFieldErrorCode("userRegistrationDTO", "userEmail", "Email"))
                .andExpect(model().attributeHasFieldErrorCode("userRegistrationDTO", "confirmEmail", "Email"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"));
    }

    @Test
    @DisplayName("Caso de Teste CT11 – Caso Inválido – Email sem domínio")
    void ct11() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abc")
                        .param("userEmail", "a@")
                        .param("confirmEmail", "a@")
                        .param("userPassword", "123456")
                        .param("confirmPassword", "123456")
                        .param("userCpf", "056.491.310-31"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "userEmail"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "confirmEmail"))
                .andExpect(model().attributeHasFieldErrorCode("userRegistrationDTO", "userEmail", "Email"))
                .andExpect(model().attributeHasFieldErrorCode("userRegistrationDTO", "confirmEmail", "Email"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"));
    }

    @Test
    @DisplayName("Caso de Teste CT12 – Caso Inválido – Email com espaço antes de @")
    void ct12() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abc")
                        .param("userEmail", "a @a")
                        .param("confirmEmail", "a @a")
                        .param("userPassword", "123456")
                        .param("confirmPassword", "123456")
                        .param("userCpf", "056.491.310-31"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "userEmail"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "confirmEmail"))
                .andExpect(model().attributeHasFieldErrorCode("userRegistrationDTO", "userEmail", "Email"))
                .andExpect(model().attributeHasFieldErrorCode("userRegistrationDTO", "confirmEmail", "Email"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"));
    }

    @Test
    @DisplayName("Caso de Teste CT13 – Caso Inválido – Email com espaço após @")
    void ct13() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abc")
                        .param("userEmail", "a@ a")
                        .param("confirmEmail", "a@ a")
                        .param("userPassword", "123456")
                        .param("confirmPassword", "123456")
                        .param("userCpf", "056.491.310-31"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "userEmail"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "confirmEmail"))
                .andExpect(model().attributeHasFieldErrorCode("userRegistrationDTO", "userEmail", "Email"))
                .andExpect(model().attributeHasFieldErrorCode("userRegistrationDTO", "confirmEmail", "Email"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"));
    }

    @Test
    @DisplayName("Caso de Teste CT14 – Email de confirmação diferente")
    void ct14() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abc")
                        .param("userEmail", "a@a")
                        .param("confirmEmail", "b@a")
                        .param("userPassword", "123456")
                        .param("confirmPassword", "123456")
                        .param("userCpf", "056.491.310-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "confirmEmail"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.userRegistrationDTO",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("Os e-mails não coincidem."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("Caso de Teste CT15 – Senha menor que 5 caracteres")
    void ct15() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abc")
                        .param("userEmail", "a@a")
                        .param("confirmEmail", "a@a")
                        .param("userPassword", "1234")
                        .param("confirmPassword", "1234")
                        .param("userCpf", "056.491.310-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "userPassword"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.userRegistrationDTO",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("A senha deve ter pelo menos 5 caracteres."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("Caso de Teste CT16 – Senhas diferentes")
    void ct16_senhasDiferentes() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abc")
                        .param("userEmail", "a@a")
                        .param("confirmEmail", "a@a")
                        .param("userPassword", "12345")
                        .param("confirmPassword", "54321")
                        .param("userCpf", "056.491.310-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "confirmPassword"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.userRegistrationDTO",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("As senhas não coincidem."))
                                )
                        )
                ));
    }

    // Este teste não é validado pelo back, apenas pelo front
    @Test
    @DisplayName("Caso de Teste CT17 – CPF sem pontos (formato aceito, backend válido)")
    void ct17() throws Exception {
        Mockito.doNothing().when(authFacade).registrarUsuario(any(UserRegistrationDTO.class));

        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abc")
                        .param("userEmail", "a@a")
                        .param("confirmEmail", "a@a")
                        .param("userPassword", "12345")
                        .param("confirmPassword", "12345")
                        .param("userCpf", "056491310-31"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("successMessage", "Conta criada com sucesso!!!"));
    }

    @Test
    @DisplayName("Caso de Teste CT18 – Caso Inválido – CPF sem hífen")
    void ct18() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abc")
                        .param("userEmail", "a@a")
                        .param("confirmEmail", "b@a")
                        .param("userPassword", "12345")
                        .param("confirmPassword", "12346")
                        .param("userCpf", "056.491.31031"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "userCpf"))
                .andExpect(model().attributeHasFieldErrorCode("userRegistrationDTO", "userCpf", "CPF"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.userRegistrationDTO",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("CPF inválido."))
                                )
                        )
                ));

    }

    @Test
    @DisplayName("Caso de Teste CT19 – Caso Inválido – CPF com letras")
    void ct19() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abc")
                        .param("userEmail", "a@a")
                        .param("confirmEmail", "b@a")
                        .param("userPassword", "12345")
                        .param("confirmPassword", "12346")
                        .param("userCpf", "056.491.310-3A"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "userCpf"))
                .andExpect(model().attributeHasFieldErrorCode("userRegistrationDTO", "userCpf", "CPF"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.userRegistrationDTO",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("CPF inválido."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("Caso de Teste CT20 – CPF com menos de 11 dígitos")
    void ct20() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abc")
                        .param("userEmail", "a@a")
                        .param("confirmEmail", "a@a")
                        .param("userPassword", "123456")
                        .param("confirmPassword", "123456")
                        .param("userCpf", "056.491.310-3"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "userCpf"))
                .andExpect(model().attributeHasFieldErrorCode("userRegistrationDTO", "userCpf", "CPF"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.userRegistrationDTO",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("CPF inválido."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("Caso de Teste CT21 – Caso Inválido – CPF com mais de 11 dígitos")
    void ct21() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abc")
                        .param("userEmail", "a@a")
                        .param("confirmEmail", "a@a")
                        .param("userPassword", "123456")
                        .param("confirmPassword", "123456")
                        .param("userCpf", "056.491.310-311"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "userCpf"))
                .andExpect(model().attributeHasFieldErrorCode("userRegistrationDTO", "userCpf", "CPF"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.userRegistrationDTO",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("CPF inválido."))
                                )
                        )
                ));
    }

    @Test
    @DisplayName("Caso de Teste CT22 – CPF inválido")
    void ct22() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("userName", "abc")
                        .param("userEmail", "a@a")
                        .param("confirmEmail", "a@a")
                        .param("userPassword", "123456")
                        .param("confirmPassword", "123456")
                        .param("userCpf", "999.999.99-99"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeHasFieldErrors("userRegistrationDTO", "userCpf"))
                .andExpect(model().attributeHasFieldErrorCode("userRegistrationDTO", "userCpf", "CPF"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Há erros no formulário!"))
                .andExpect(model().attribute(
                        "org.springframework.validation.BindingResult.userRegistrationDTO",
                        org.hamcrest.Matchers.hasProperty("fieldErrors",
                                org.hamcrest.Matchers.hasItem(
                                        org.hamcrest.Matchers.hasProperty("defaultMessage",
                                                org.hamcrest.Matchers.is("CPF inválido."))
                                )
                        )
                ));
    }
}