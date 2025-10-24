package com.gymtutor.gymtutor.config;

import com.gymtutor.gymtutor.admin.activities.MuscularGroup;
import com.gymtutor.gymtutor.admin.activities.MuscularGroupModel;
import com.gymtutor.gymtutor.admin.activities.MuscularGroupRepository;
import com.gymtutor.gymtutor.user.Role;
import com.gymtutor.gymtutor.user.RoleName;
import com.gymtutor.gymtutor.user.RoleRepository;
import com.gymtutor.gymtutor.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    private static DataLoader instancia; // instância única da classe

    private final RoleRepository roleRepository;
    private final UserService userService;
    private final MuscularGroupRepository muscularGroupRepository;

    // Construtor privado (impede criação manual fora da classe)
    private DataLoader(RoleRepository roleRepository, UserService userService, MuscularGroupRepository muscularGroupRepository) {
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.muscularGroupRepository = muscularGroupRepository;
        instancia = this; // armazena a instância criada pelo Spring
    }

    // Metodo de acesso à instância única
    public static DataLoader getInstance() {
        return instancia;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(RoleName.ADMIN));
            roleRepository.save(new Role(RoleName.STUDENT));
            roleRepository.save(new Role(RoleName.PERSONAL));
        }

        if (muscularGroupRepository.count() == 0) {
            Arrays.stream(MuscularGroup.values())
                    .map(MuscularGroupModel::new)
                    .forEach(muscularGroupRepository::save);
        }

        userService.createAdminUser();
    }
}