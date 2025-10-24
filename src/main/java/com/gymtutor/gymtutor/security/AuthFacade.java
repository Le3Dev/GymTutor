package com.gymtutor.gymtutor.security.facade;

import com.gymtutor.gymtutor.user.*;
import com.gymtutor.gymtutor.security.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class AuthFacade {

    private final UserRepository userRepository;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationFailureHandler failureHandler;

    public AuthFacade(UserRepository userRepository,
                      UserService userService,
                      CustomUserDetailsService customUserDetailsService,
                      CustomAuthenticationFailureHandler failureHandler) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
        this.failureHandler = failureHandler;
    }

    // --- REGISTRO ---
    public void registrarUsuario(UserRegistrationDTO dto) {
        try {
            userService.createUser(dto);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("E-mail já registrado no sistema.");
        }
    }

    // --- RECUPERAÇÃO DE SENHA ---
    public void recuperarSenha(UserRecoveryPasswordDTO dto) {
        User user = userRepository.findByUserEmail(dto.getUserEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        userService.enableUser(user);
    }

}