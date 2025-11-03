package com.gymtutor.gymtutor.admin.crefreview;

import com.gymtutor.gymtutor.user.Personal;
import com.gymtutor.gymtutor.user.PersonalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CrefReviewControllerTest {

    @Mock
    private PersonalService personalService;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private CrefReviewController crefReviewController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Caso de Teste CT01 – Caso Valido - No Limite Inferior")
    void ct01() {
        Personal personal = new Personal();
        personal.setPersonalId(1);

        when(personalService.findById(1)).thenReturn(personal);

        String result = crefReviewController.rejectCref(1, "abcde", redirectAttributes);

        verify(personalService).rejectPersonal(personal, "abcde");
        verify(redirectAttributes).addFlashAttribute("successMessage", "CREF rejeitado com sucesso.");
        assertEquals("redirect:/admin/cref_review", result);
    }

    @Test
    @DisplayName("Caso de Teste CT02 – Caso Valido - No Limite Superior")
    void ct02() {
        Personal personal = new Personal();
        personal.setPersonalId(2);
        String motivo = "a".repeat(50);

        when(personalService.findById(2)).thenReturn(personal);

        String result = crefReviewController.rejectCref(2, motivo, redirectAttributes);

        verify(personalService).rejectPersonal(personal, motivo);
        verify(redirectAttributes).addFlashAttribute("successMessage", "CREF rejeitado com sucesso.");
        assertEquals("redirect:/admin/cref_review", result);
    }

    @Test
    @DisplayName("Caso de Teste CT03 – Caso Valido – Acima do Limite Inferior")
    void ct03() {
        Personal personal = new Personal();
        personal.setPersonalId(3);

        when(personalService.findById(3)).thenReturn(personal);

        String result = crefReviewController.rejectCref(3, "abcdef", redirectAttributes);

        verify(personalService).rejectPersonal(personal, "abcdef");
        verify(redirectAttributes).addFlashAttribute("successMessage", "CREF rejeitado com sucesso.");
        assertEquals("redirect:/admin/cref_review", result);
    }

    @Test
    @DisplayName("Caso de Teste CT04 – Caso Valido - Abaixo do Limite Superior")
    void ct04() {
        Personal personal = new Personal();
        personal.setPersonalId(4);
        String motivo = "a".repeat(49);

        when(personalService.findById(4)).thenReturn(personal);

        String result = crefReviewController.rejectCref(4, motivo, redirectAttributes);

        verify(personalService).rejectPersonal(personal, motivo);
        verify(redirectAttributes).addFlashAttribute("successMessage", "CREF rejeitado com sucesso.");
        assertEquals("redirect:/admin/cref_review", result);
    }

    @Test
    @DisplayName("Caso de Teste CT05 – Caso Invalido – Motivo Vazio")
    void ct05() {
        String result = crefReviewController.rejectCref(5, "", redirectAttributes);

        verify(personalService, never()).rejectPersonal(any(), any());
        verify(redirectAttributes).addFlashAttribute("errorMessage", "O motivo da recusa deve ter entre 5 e 50 caracteres.");
        assertEquals("redirect:/admin/cref_review", result);
    }

    @Test
    @DisplayName("Caso de Teste CT06 – Caso Invalido – Motivo Abaixo do Limite Inferior")
    void ct06() {
        String result = crefReviewController.rejectCref(6, "abcd", redirectAttributes);

        verify(personalService, never()).rejectPersonal(any(), any());
        verify(redirectAttributes).addFlashAttribute("errorMessage", "O motivo da recusa deve ter entre 5 e 50 caracteres.");

        assertEquals("redirect:/admin/cref_review", result);
    }

    @Test
    @DisplayName("Caso de Teste CT07 – Caso Invalido – Motivo Acima do Limite Superior")
    void ct07() {
        String motivo = "a".repeat(51);
        String result = crefReviewController.rejectCref(7, motivo, redirectAttributes);

        verify(personalService, never()).rejectPersonal(any(), any());
        verify(redirectAttributes).addFlashAttribute("errorMessage", "O motivo da recusa deve ter entre 5 e 50 caracteres.");
        assertEquals("redirect:/admin/cref_review", result);
    }

}