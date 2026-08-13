package com.atchayamart.service;

import com.atchayamart.dto.RegisterRequest;
import com.atchayamart.exception.AuthException;
import com.atchayamart.model.Role;
import com.atchayamart.model.User;
import com.atchayamart.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(RegisterRequest request) {
        Role role = request.getRole();
        if (role == null) {
            role = Role.BUYER;
        }
        if (role == Role.ADMIN) {
            throw new AuthException("Admin accounts cannot be registered");
        }
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new AuthException("An account with this email already exists");
        }
        User user = new User(email, request.getName().trim(), passwordEncoder.encode(request.getPassword()), role);
        return userRepository.save(user);
    }

    public User login(String email, String rawPassword) {
        String normalized = email.trim().toLowerCase();
        User user = userRepository.findByEmail(normalized)
                .orElseThrow(() -> new AuthException("Invalid email or password"));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new AuthException("Invalid email or password");
        }
        return user;
    }
}