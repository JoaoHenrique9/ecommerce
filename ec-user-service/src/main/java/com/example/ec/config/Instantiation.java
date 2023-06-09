package com.example.ec.config;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.example.ec.models.Role;
import com.example.ec.models.UserModel;
import com.example.ec.repositories.RoleRepository;
import com.example.ec.repositories.UserRepository;
import com.example.ec.utils.StringUtils;

@Configuration
public class Instantiation implements CommandLineRunner {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private RoleRepository roleRepository;

        @Override
        public void run(String... args) throws Exception {

                // Limpar os dados existentes
                userRepository.deleteAll();
                roleRepository.deleteAll();

                // Inclusão dos produtos e categorias a partir do seed

                // Usuarios
                UserModel user0 = UserModel.builder()
                                .name("Admin")
                                .email("admin@admin.com")
                                .password(StringUtils.encoder("123456"))
                                .build();
                UserModel user1 = UserModel.builder()
                                .name("John Wick")
                                .email("john@gmail.com")
                                .password(StringUtils.encoder("S3nh@"))
                                .build();
                UserModel user2 = UserModel.builder()
                                .name("Morpheus")
                                .email("morpheus@gmail.com")
                                .password(StringUtils.encoder("S3nh@"))
                                .build();

                userRepository.saveAll(Arrays.asList(user1, user2));

                // Role
                Role admin = Role.builder().id(1L).roleName("ROLE_ADMIN").build();
                Role user = Role.builder().id(2L).roleName("ROLE_USER").build();

                roleRepository.saveAll(Arrays.asList(admin, user));

                user0.setRoles(new HashSet<Role>(Arrays.asList(admin, user)));
                user1.setRoles(new HashSet<Role>(Arrays.asList(admin, user)));
                user2.setRoles(new HashSet<Role>(Arrays.asList(user)));

                userRepository.saveAll(Arrays.asList(user0, user1, user2));

        }
}
