package com.gymtutor.gymtutor.security;

import com.gymtutor.gymtutor.security.facade.AuthFacade;
import com.gymtutor.gymtutor.user.UserRecoveryPasswordDTO;
import com.gymtutor.gymtutor.user.UserRegistrationDTO;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthFacade authFacade;

    public AuthController(AuthFacade authFacade) {
        this.authFacade = authFacade;
    }

    // Página de login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Página de registro
    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRegistrationDTO", new UserRegistrationDTO());
        return "registration";
    }

    // Processa o registro de novo usuário
    @PostMapping("/registration")
    public String processRegistration(
            @Valid @ModelAttribute UserRegistrationDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Há erros no formulário!");
            return "registration";
        }

        return handleRequest(redirectAttributes, model, "registration", dto, () -> {
            authFacade.registrarUsuario(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Conta criada com sucesso!");
            return "redirect:/login";
        });
    }

    // Página de recuperação de senha
    @GetMapping("/password-recovery")
    public String showRecoveryForm(Model model) {
        model.addAttribute("userRecoveryPasswordDTO", new UserRecoveryPasswordDTO());
        return "password-recovery";
    }

    // Processa a recuperação de senha
    @PostMapping("/password-recovery")
    public String processPasswordRecovery(
            @Valid @ModelAttribute UserRecoveryPasswordDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Há erros no formulário!");
            return "password-recovery";
        }

        return handleRequest(redirectAttributes, model, "password-recovery", dto, () -> {
            authFacade.recuperarSenha(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Um e-mail foi enviado para a sua conta!");
            return "redirect:/login";
        });
    }

    private String handleRequest(
            RedirectAttributes redirectAttributes,
            Model model,
            String view,
            Object anyDTO,
            RequestHandler block
    ) {
        try {
            return block.execute();
        } catch (Exception ex) {
            return handleException(ex, model, anyDTO, view, redirectAttributes);
        }
    }

    @FunctionalInterface
    public interface RequestHandler {
        String execute();
    }

    private String handleException(
            Exception ex,
            Model model,
            Object anyDTO,
            String view,
            RedirectAttributes redirectAttributes
    ) {
        return switch (ex) {
            case IllegalArgumentException illegalArgumentException ->
                    handleIllegalArgumentException(illegalArgumentException, model, anyDTO, view);
            case IllegalAccessException illegalAccessException ->
                    handleIllegalAccessException(illegalAccessException, redirectAttributes);
            case DataIntegrityViolationException ignored ->
                    handleDataIntegrityViolationException(model, anyDTO, view);
            case null, default -> handleUnexpectedException(model, anyDTO, view);
        };
    }

    private String handleIllegalArgumentException(
            IllegalArgumentException ex,
            Model model,
            Object anyDTO,
            String view
    ) {
        return handleError(ex.getMessage(), model, anyDTO, view);
    }

    private String handleIllegalAccessException(
            IllegalAccessException ex,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/login";
    }

    private String handleDataIntegrityViolationException(Model model, Object anyDTO, String view) {
        return handleError("Erro de integridade de dados.", model, anyDTO, view);
    }

    private String handleUnexpectedException(Model model, Object anyDTO, String view) {
        return handleError("Erro inesperado. Tente novamente.", model, anyDTO, view);
    }

    private String handleError(String errorMessage, Model model, Object anyDTO, String view) {
        if (model != null) {
            model.addAttribute("errorMessage", errorMessage);
            if (anyDTO instanceof UserRegistrationDTO) {
                model.addAttribute("userRegistrationDTO", anyDTO);
            } else if (anyDTO instanceof UserRecoveryPasswordDTO) {
                model.addAttribute("userRecoveryPasswordDTO", anyDTO);
            }
        }
        return (view != null) ? view : "redirect:/login";
    }
}