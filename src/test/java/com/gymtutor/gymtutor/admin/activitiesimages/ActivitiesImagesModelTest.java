package com.gymtutor.gymtutor.admin.activitiesimages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActivitiesImagesModelTest {

    private final LocalValidatorFactoryBean validator;

    ActivitiesImagesModelTest() {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
    }

    private boolean hasMessage(BindingResult result, String expectedMessage) {
        return result.getAllErrors().stream()
                .anyMatch(error -> expectedMessage.equals(error.getDefaultMessage()));
    }

    @Test
    @DisplayName("CT01 – Caso Valido - No Limite Inferior")
    void ct01() {
        ActivitiesImagesModel model = new ActivitiesImagesModel();
        model.setImageName("abc");

        BindingResult result = new BeanPropertyBindingResult(model, "activitiesImagesModel");
        validator.validate(model, result);

        assertTrue(!result.hasErrors(), "Deveria ser válido");
    }

    @Test
    @DisplayName("CT02 – Caso Valido - No Limite Superior")
    void ct02() {
        ActivitiesImagesModel model = new ActivitiesImagesModel();
        model.setImageName("a".repeat(50));

        BindingResult result = new BeanPropertyBindingResult(model, "activitiesImagesModel");
        validator.validate(model, result);

        assertTrue(!result.hasErrors(), "Deveria ser válido");
    }

    @Test
    @DisplayName("CT03 – Caso Valido – Acima do Limite Inferior")
    void ct03() {
        ActivitiesImagesModel model = new ActivitiesImagesModel();
        model.setImageName("abcd");

        BindingResult result = new BeanPropertyBindingResult(model, "activitiesImagesModel");
        validator.validate(model, result);

        assertTrue(!result.hasErrors(), "Deveria ser válido");
    }

    @Test
    @DisplayName("CT04 – Caso Valido - Abaixo do Limite Superior")
    void ct04() {
        ActivitiesImagesModel model = new ActivitiesImagesModel();
        model.setImageName("a".repeat(49));

        BindingResult result = new BeanPropertyBindingResult(model, "activitiesImagesModel");
        validator.validate(model, result);

        assertTrue(!result.hasErrors(), "Deveria ser válido");
    }

    @Test
    @DisplayName("CT05 – Caso Invalido – Nome Vazio")
    void ct05() {
        ActivitiesImagesModel model = new ActivitiesImagesModel();
        model.setImageName("");

        BindingResult result = new BeanPropertyBindingResult(model, "activitiesImagesModel");
        validator.validate(model, result);

        assertTrue(result.hasErrors(), "Deve haver erro de validação para nome vazio");
        assertTrue(
                hasMessage(result, "Nome da imagem não pode estar vazio!"),
                "Mensagem esperada: 'Nome da imagem não pode estar vazio!'"
        );
    }



    @Test
    @DisplayName("CT07 – Caso Invalido – Nome Abaixo do Limite Inferior")
    void ct07() {
        ActivitiesImagesModel model = new ActivitiesImagesModel();
        model.setImageName("ab");

        BindingResult result = new BeanPropertyBindingResult(model, "activitiesImagesModel");
        validator.validate(model, result);

        assertTrue(result.hasErrors(), "Deveria apresentar erro de nome curto");
        assertTrue(
                hasMessage(result, "Nome da imagem deve ter entre 3 e 50 caracteres."),
                "Mensagem esperada: 'Nome da imagem deve ter entre 3 e 50 caracteres.'"
        );
    }

    @Test
    @DisplayName("CT08 – Caso Invalido – Nome Acima do Limite Superior")
    void ct08() {
        ActivitiesImagesModel model = new ActivitiesImagesModel();
        model.setImageName("a".repeat(51));

        BindingResult result = new BeanPropertyBindingResult(model, "activitiesImagesModel");
        validator.validate(model, result);

        assertTrue(result.hasErrors(), "Deveria apresentar erro de nome longo");
        assertTrue(
                hasMessage(result, "Nome da imagem deve ter entre 3 e 50 caracteres."),
                "Mensagem esperada: 'Nome da imagem deve ter entre 3 e 50 caracteres.'"
        );
    }

}