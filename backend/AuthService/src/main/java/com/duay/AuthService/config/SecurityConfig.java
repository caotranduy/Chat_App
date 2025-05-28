package com.duay.AuthService.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.duay.AuthService.security.JwtFilter;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor; // Đảm bảo import đúng CorsFilter này

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .disable())
                .authorizeHttpRequests(auth ->  auth
                .requestMatchers("/home")
                .permitAll()
                .dispatcherTypeMatchers(DispatcherType.ERROR)
                .permitAll()
                .anyRequest()
                .authenticated())
            // Thêm CorsFilter vào đây
            .cors(cors -> cors.configurationSource(corsFilterSource())) // Sử dụng corsFilterSource
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Các API đăng nhập/đăng ký được phép
                .requestMatchers("/", "/login", "/register").permitAll() // Các route SPA HTML
                //.requestMatchers("/css/**", "/js/**", "/lang/**").permitAll() // Các file tĩnh
                .anyRequest().authenticated() // Các request khác yêu cầu xác thực
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //Thay đổi tên phương thức để rõ ràng hơn và trả về UrlBasedCorsConfigurationSource
    @Bean
    public UrlBasedCorsConfigurationSource corsFilterSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("*")); // Cho phép mọi nguồn gốc (cẩn thận với production)
        config.setAllowedHeaders(List.of("*")); // Cho phép mọi header
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Cho phép mọi phương thức HTTP

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Áp dụng cấu hình CORS cho mọi đường dẫn
        return source;
    }
}