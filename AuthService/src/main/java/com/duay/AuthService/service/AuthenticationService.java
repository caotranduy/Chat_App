package com.duay.AuthService.service;

import com.duay.AuthService.dto.AuthenticationRequest;
import com.duay.AuthService.dto.AuthenticationResponse;
import com.duay.AuthService.dto.RegisterRequest;
import com.duay.AuthService.model.Role;
import com.duay.AuthService.model.User;
import com.duay.AuthService.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Đăng ký người dùng mới
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken!"); // Xử lý khi username đã tồn tại
        }

        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // Mã hóa mật khẩu
                .role(Role.USER) // Mặc định là ROLE_USER
                .build();
        userRepository.save(user); // Lưu người dùng vào DB
        var jwtToken = jwtService.generateToken(user); // Tạo JWT
        var refreshToken = jwtService.generateRefreshToken(user); // Tạo Refresh Token
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Đăng nhập người dùng
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate( // Xác thực người dùng bằng username và password
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(); // Người dùng đã được xác thực, nên tìm thấy là chắc chắn

        var jwtToken = jwtService.generateToken(user); // Tạo JWT
        var refreshToken = jwtService.generateRefreshToken(user); // Tạo Refresh Token
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Làm mới token
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            var user = this.userRepository.findByUsername(username)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) { // Kiểm tra Refresh Token có hợp lệ không
                var accessToken = jwtService.generateToken(user); // Tạo Access Token mới
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken) // Giữ nguyên Refresh Token cũ hoặc tạo mới nếu muốn
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse); // Ghi response
            }
        }
    }
}