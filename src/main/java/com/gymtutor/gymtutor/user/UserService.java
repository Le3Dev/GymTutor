package com.gymtutor.gymtutor.user;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PersonalRepository personalRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    //Construtor
    public UserService(UserRepository userRepository, PersonalRepository personalRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.personalRepository = personalRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createUser(UserRegistrationDTO userRegistrationDTO) {
        // Criptografando a senha

        userRepository.findByUserEmail(userRegistrationDTO.getUserEmail())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Já existe um Usuário com este e-mail cadastrado.");
                });

        userRepository.findByUserCpf(userRegistrationDTO.getUserCpf())
                .ifPresent( user -> {
                    throw new IllegalArgumentException("Já existe um Usuário com este CPF cadastrado.");
                });

        User user = new User();
        user.setUserPassword(passwordEncoder.encode(userRegistrationDTO.getUserPassword()));
        user.setUserName(userRegistrationDTO.getUserName());
        user.setUserEmail(userRegistrationDTO.getUserEmail());
        user.setUserCpf(userRegistrationDTO.getUserCpf());

        // Associando a role 'STUDENT' por padrão
        Role studentRole = roleRepository.findByRoleName(RoleName.STUDENT);
        user.setRole(studentRole);

        // Salvando o usuário
        userRepository.save(user);
    }

    public void createAdminUser() {
        if (userRepository.findByUserEmail("admin@academia.com").isEmpty()) {
            User admin = new User();
            admin.setUserName("Administrador");
            admin.setUserEmail("admin@academia.com");
            admin.setUserPassword(passwordEncoder.encode("admin123"));

            // Adicionando a role 'ADMIN' para o administrador
            Role adminRole = roleRepository.findByRoleName(RoleName.ADMIN);
            admin.setRole(adminRole);

            userRepository.save(admin);
        }
    }

    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getUserPassword());
    }

    public void changePassword(User user, String newPassword) {
        user.setUserPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    //TODO: Adicionar validação para impedir administradores de virarem personal
    public void changeRole(User user, RoleName newRoleName) {
        if (user.getRole().getRoleName() == RoleName.ADMIN && newRoleName == RoleName.PERSONAL) {
            throw new IllegalArgumentException("Administrador não pode ser um Personal Trainer.");
        }

        Role newRole = roleRepository.findByRoleName(newRoleName);
        user.setRole(newRole);
        userRepository.save(user);
    }

    public void disableUser(User user) {
        user.setActive(false);
        userRepository.save(user);
    }

    public void enableUser(User user) {
        user.setActive(true);
        userRepository.save(user);
    }


}
