package com.duay.AuthService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.duay.AuthService.dto.LoginRequest;
import com.duay.AuthService.dto.RegisterRequest;
import com.duay.AuthService.entity.User;
import com.duay.AuthService.repository.UserRepository;
import com.duay.AuthService.security.JwtUtil;

@Service
public class AuthService {
    @Autowired UserRepository userRepo;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JwtUtil jwtUtil;

    public void register(RegisterRequest req) {
        if (userRepo.findByUsername(req.username).isPresent()) {
            throw new RuntimeException("Username already taken");
        }
        User user = new User();
        user.setUsername(req.username);
        user.setPassword(passwordEncoder.encode(req.password));
        user.setDisplayName(req.displayName);
        userRepo.save(user);
    }

    public String login(LoginRequest req) {
        User user = userRepo.findByUsername(req.username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(req.password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return jwtUtil.generateToken(user.getUsername());
    }
}

