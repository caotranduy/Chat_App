package com.duay.AuthService.service;

import java.io.IOException;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.duay.AuthService.dto.AuthenticationRequest;
import com.duay.AuthService.dto.AuthenticationResponse;
import com.duay.AuthService.dto.RegisterRequest;
import com.duay.AuthService.dto.RegisterResponse;
import com.duay.AuthService.model.UserAuthInfo.Role;
import com.duay.AuthService.model.UserAuthInfo.User;
import com.duay.AuthService.repository.UserCredentialRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserCredentialRepository userCredentialRepository;
    private final UserProfileService userProfileService; 
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /*Đăng ký người dùng mới*/
    public RegisterResponse register(RegisterRequest request) {

        // 1. Kiểm tra xem username đã tồn tại chưa
        if (userCredentialRepository.existsByUsername(request.getUsername())) {
            return RegisterResponse.builder()
                    .resultCode(RegisterResponse.ResultCode.USERNAME_EXISTS)
                    .username(request.getUsername()) 
                    .build();
        }

        if (userCredentialRepository.existsByEmail(request.getEmail())) {
            return RegisterResponse.builder()
                    .resultCode(RegisterResponse.ResultCode.EMAIL_EXISTS)
                    .username(request.getUsername()) 
                    .email(request.getEmail()) 
                    .build();
        }

        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) 
                .email(request.getEmail())
                .role(Role.USER) // ROLE_USER by default
                .build();

        try {
            User savedUser = userCredentialRepository.save(user);
            try {
                userProfileService.initializeUserProfile(savedUser.getId()); //Initialize user profile after saving user
            } catch (Exception e) {
                System.err.println("Error when initializing user profile: " + e.getMessage());
            }
            return RegisterResponse.builder()
                    .resultCode(RegisterResponse.ResultCode.SUCCESS)
                    .username(savedUser.getUsername()) 
                    .build();
        } catch (DataAccessException e) {
            System.err.println("Error when saving user: " + e.getMessage());
            return RegisterResponse.builder()
                    .resultCode(RegisterResponse.ResultCode.ERROR) 
                    .username(request.getUsername())
                    .build();
        }
    }

    // Đăng nhập người dùng
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate( 
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userCredentialRepository.findByUsername(request.getUsername())
                .orElseThrow(); 

        var jwtToken = jwtService.generateToken(user); 
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                //.user("")
                .build();
    }
// public AuthenticationResponse authenticate(AuthenticationRequest request) {
//         // BƯỚC 1: Xác thực người dùng.
//         // Nếu thông tin đăng nhập không đúng, dòng này sẽ ném ra ngoại lệ
//         // AuthenticationException. Bạn cần xử lý ngoại lệ này ở tầng cao hơn
//         // hoặc để Spring Security xử lý mặc định (thường trả về lỗi 401 Unauthorized).
//         authenticationManager.authenticate(
//                 new UsernamePasswordAuthenticationToken(
//                         request.getUsername(),
//                         request.getPassword()
//                 )
//         );

//         // BƯỚC 2: Tìm người dùng trong cơ sở dữ liệu sau khi xác thực thành công.
//         // .orElseThrow() sẽ ném ra NoSuchElementException nếu không tìm thấy,
//         // nhưng sau khi xác thực thành công thì thường là tìm thấy.
//         var user = userCredentialRepository.findByUsername(request.getUsername())
//                 .orElseThrow(() -> new RuntimeException("User not found after authentication")); // Thêm thông báo rõ ràng hơn

//         // BƯỚC 3: TẠM THỜI BYPASS VIỆC TẠO VÀ TRẢ VỀ JWT
//         // Các dòng này đã được bạn comment.
//         // var jwtToken = jwtService.generateToken(user);
//         // var refreshToken = jwtService.generateRefreshToken(user);

//         // BƯỚC 4: Trả về một AuthenticationResponse đơn giản để kiểm tra.
//         // Thay vì user(""), bạn có thể trả về một chuỗi nào đó đại diện cho user
//         // hoặc null nếu AuthenticationResponse cho phép.
//         // Tốt nhất là trả về một giá trị có ý nghĩa để xác nhận debug.
//         return AuthenticationResponse.builder()
//                 // .accessToken(jwtToken) // Comment lại
//                 // .refreshToken(refreshToken) // Comment lại
//                 // Đặt một giá trị debug tại đây, ví dụ: tên người dùng
//                 .user(user.getUsername()) // Giả sử user có phương thức getUsername()
//                 .build();
//     }

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
            var user = this.userCredentialRepository.findByUsername(username)
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

    public void deleteUser(String username) {
        var user = userCredentialRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userCredentialRepository.delete(user); // Xóa người dùng khỏi DB
    }
}