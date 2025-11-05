package com.gymtutor.gymtutor.user;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonalCrefDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Caso de Teste CT01 – Caso Valido - No Limite Inferior")
    void ct01() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("1-G");
        dto.setState(State.AC);
        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver erros de validação");
    }

    @Test
    @DisplayName("Caso de Teste CT02 – Caso Valido - No Limite Superior")
    void ct02() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("123456-G");
        dto.setState(State.AC);

        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver erros de validação");
    }

    @Test
    @DisplayName("Caso de Teste CT03 – Caso Valido – Acima do Limite Inferior")
    void ct03() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("12-G");
        dto.setState(State.AC);

        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver erros de validação");
    }

    @Test
    @DisplayName("Caso de Teste CT04 – Caso Valido - Abaixo do Limite Superior")
    void ct04() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("12345-G");
        dto.setState(State.AC);

        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Não deve haver erros de validação");
    }

    @Test
    @DisplayName("Caso de Teste CT05 – Caso Invalido – CREF Vazio")
    void ct05() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("");
        dto.setState(State.AC);

        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver erro de validação para CREF vazio");

        boolean hasExpectedMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals("O CREF é obrigatório."));
        assertTrue(hasExpectedMessage, "Mensagem de erro deve ser 'O CREF é obrigatório.'");
    }

    @Test
    @DisplayName("Caso de Teste CT07 – Caso Invalido – CREF Abaixo do Limite Inferior")
    void ct07() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("-C"); // inválido
        dto.setState(State.AC);

        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver erro de validação para CREF abaixo do limite inferior");

        boolean hasExpectedMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals(
                        "O CREF deve conter de 1 a 6 dígitos seguidos de um hífen e uma letra (G, P ou C). Exemplo: 123456-G"
                ));
        assertTrue(hasExpectedMessage, "Mensagem de erro deve ser referente ao formato do CREF");
    }

    @Test
    @DisplayName("Caso de Teste CT08 – Caso Invalido – CREF Acima do Limite Superior")
    void ct08() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("1234567-C"); // inválido
        dto.setState(State.AC);

        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver erro de validação para CREF acima do limite superior");

        boolean hasExpectedMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals(
                        "O CREF deve conter de 1 a 6 dígitos seguidos de um hífen e uma letra (G, P ou C). Exemplo: 123456-G"
                ));
        assertTrue(hasExpectedMessage, "Mensagem de erro deve ser referente ao formato do CREF");
    }

    @Test
    @DisplayName("Caso de Teste CT09 – Caso Invalido – CREF Sem o Hífen")
    void ct09() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("123456C"); // inválido
        dto.setState(State.AC);

        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver erro de validação para CREF sem o hífen");

        boolean hasExpectedMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals(
                        "O CREF deve conter de 1 a 6 dígitos seguidos de um hífen e uma letra (G, P ou C). Exemplo: 123456-G"
                ));
        assertTrue(hasExpectedMessage, "Mensagem de erro deve ser referente ao formato do CREF");
    }

    @Test
    @DisplayName("Caso de Teste CT10 – Caso Invalido – CREF Com 2 Hifens")
    void ct10() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("123456--C"); // inválido
        dto.setState(State.AC);

        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver erro de validação para CREF com 2 hifens");

        boolean hasExpectedMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals(
                        "O CREF deve conter de 1 a 6 dígitos seguidos de um hífen e uma letra (G, P ou C). Exemplo: 123456-G"
                ));
        assertTrue(hasExpectedMessage, "Mensagem de erro deve ser referente ao formato do CREF");
    }

    @Test
    @DisplayName("Caso de Teste CT11 – Caso Invalido – CREF Sem Nenhuma Letra")
    void ct11() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("123456-");
        dto.setState(State.AC);

        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver erro de validação para CREF sem letra");

        boolean hasExpectedMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals(
                        "O CREF deve conter de 1 a 6 dígitos seguidos de um hífen e uma letra (G, P ou C). Exemplo: 123456-G"
                ));
        assertTrue(hasExpectedMessage, "Mensagem de erro deve ser referente ao formato do CREF");
    }

    @Test
    @DisplayName("Caso de Teste CT12 – Caso Invalido – CREF Com 2 Letras")
    void ct12() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("123456-CC");
        dto.setState(State.AC);

        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver erro de validação para CREF com 2 letras");

        boolean hasExpectedMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals(
                        "O CREF deve conter de 1 a 6 dígitos seguidos de um hífen e uma letra (G, P ou C). Exemplo: 123456-G"
                ));
        assertTrue(hasExpectedMessage, "Mensagem de erro deve ser referente ao formato do CREF");
    }

    @Test
    @DisplayName("Caso de Teste CT13 – Caso Invalido – CREF Com Letra Inválida")
    void ct13() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("123456-U");
        dto.setState(State.AC);

        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver erro de validação para CREF com letra inválida");

        boolean hasExpectedMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals(
                        "O CREF deve conter de 1 a 6 dígitos seguidos de um hífen e uma letra (G, P ou C). Exemplo: 123456-G"
                ));
        assertTrue(hasExpectedMessage, "Mensagem de erro deve ser referente ao formato do CREF");
    }

    @Test
    @DisplayName("Caso de Teste CT14 – Caso Invalido – CREF Com Letra Inválida")
    void ct14() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("123456-U");
        dto.setState(State.AC);

        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver erro de validação para CREF com letra inválida");

        boolean hasExpectedMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals(
                        "O CREF deve conter de 1 a 6 dígitos seguidos de um hífen e uma letra (G, P ou C). Exemplo: 123456-G"
                ));
        assertTrue(hasExpectedMessage, "Mensagem de erro deve ser referente ao formato do CREF");
    }

    @Test
    @DisplayName("Caso de Teste CT15 – Caso Invalido – CREF Sequência incorreta")
    void ct15() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("-123456U");
        dto.setState(State.AC);

        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver erro de validação para CREF com sequência incorreta");

        boolean hasExpectedMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals(
                        "O CREF deve conter de 1 a 6 dígitos seguidos de um hífen e uma letra (G, P ou C). Exemplo: 123456-G"
                ));
        assertTrue(hasExpectedMessage, "Mensagem de erro deve ser referente ao formato do CREF");
    }

    @Test
    @DisplayName("Caso de Teste CT16 – Caso Invalido – CREF Sequência incorreta")
    void ct16() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("123456U-");
        dto.setState(State.AC);

        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver erro de validação para CREF com sequência incorreta");

        boolean hasExpectedMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals(
                        "O CREF deve conter de 1 a 6 dígitos seguidos de um hífen e uma letra (G, P ou C). Exemplo: 123456-G"
                ));
        assertTrue(hasExpectedMessage, "Mensagem de erro deve ser referente ao formato do CREF");
    }

    @Test
    @DisplayName("Caso de Teste CT17 – Caso Invalido – CREF Sequência incorreta")
    void ct17() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("C-123456");
        dto.setState(State.AC);

        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver erro de validação para CREF com sequência incorreta");

        boolean hasExpectedMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals(
                        "O CREF deve conter de 1 a 6 dígitos seguidos de um hífen e uma letra (G, P ou C). Exemplo: 123456-G"
                ));
        assertTrue(hasExpectedMessage, "Mensagem de erro deve ser referente ao formato do CREF");
    }

    @Test
    @DisplayName("Caso de Teste CT18 – Caso Invalido – CREF Sequência incorreta")
    void ct18() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("-C123456");
        dto.setState(State.AC);

        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver erro de validação para CREF com sequência incorreta");

        boolean hasExpectedMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals(
                        "O CREF deve conter de 1 a 6 dígitos seguidos de um hífen e uma letra (G, P ou C). Exemplo: 123456-G"
                ));
        assertTrue(hasExpectedMessage, "Mensagem de erro deve ser referente ao formato do CREF");
    }

    @Test
    @DisplayName("Caso de Teste CT19 – Caso Invalido – CREF Caracter Especial")
    void ct19() {
        PersonalCrefDTO dto = new PersonalCrefDTO();
        dto.setPersonalCREF("1234@-C");
        dto.setState(State.AC);

        Set<ConstraintViolation<PersonalCrefDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deve haver erro de validação para CREF com caracter especial");

        boolean hasExpectedMessage = violations.stream()
                .anyMatch(v -> v.getMessage().equals(
                        "O CREF deve conter de 1 a 6 dígitos seguidos de um hífen e uma letra (G, P ou C). Exemplo: 123456-G"
                ));
        assertTrue(hasExpectedMessage, "Mensagem de erro deve ser referente ao formato do CREF");
    }

}