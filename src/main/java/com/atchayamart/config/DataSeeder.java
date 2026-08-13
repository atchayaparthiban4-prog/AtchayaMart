package com.atchayamart.config;

import com.atchayamart.model.Role;
import com.atchayamart.model.User;
import com.atchayamart.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository) {
        return args -> {
            if (!userRepository.existsByEmail("admin@atchayamart.com")) {
                User admin = new User(
                        "admin@atchayamart.com",
                        "Administrator",
                        new BCryptPasswordEncoder().encode("admin123"),
                        Role.ADMIN);
                userRepository.save(admin);
                System.out.println("[AtchayaMart] Admin created: admin@atchayamart.com / admin123");
            }
        };
    }
}