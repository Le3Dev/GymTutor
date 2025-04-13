package com.gymtutor.gymtutor.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserChangePasswordDTO {

    @NotBlank
    @Size(min = 5, message = "A senha deve ter pelo menos 5 caracteres.")
    private String userPassword;

    @NotBlank
    private String confirmPassword;

    // Getters e Setters
    public String getUserPassword() { return userPassword; }
    public void setUserPassword(String userPassword) { this.userPassword = userPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
