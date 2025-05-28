package com.duay.AuthService.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException; // Import thêm AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.duay.AuthService.dto.AuthRequest;
import com.duay.AuthService.dto.AuthResponse;
import com.duay.AuthService.dto.RegisterRequest;
import com.duay.AuthService.entity.Role;
import com.duay.AuthService.entity.User;
import com.duay.AuthService.repository.UserRepository;
import com.duay.AuthService.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();
        
        User savedUser = repository.save(user);
        String jwtToken = jwtService.generateToken(savedUser);
        return AuthResponse.builder()
                .token(jwtToken)
                .authStatusCode(0) // Đăng ký thành công, trả về 0
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (AuthenticationException e) {
            return AuthResponse.builder()
                    .authStatusCode(-1) 
                    .token(null) 
                    .build();
        }
        
        User user = repository.findByEmail(request.email())
                .orElse(null); 

        if (user == null) {
            return AuthResponse.builder()
                    .authStatusCode(-2) 
                    .token(null) 
                    .build();
        }

        
        String jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .authStatusCode(0) 
                .build();
    }
}