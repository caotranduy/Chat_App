package com.duay.AuthService.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Áp dụng cho tất cả các endpoint mà bạn có trong controller
        registry.addMapping("/**") // Hoặc "/**" nếu muốn áp dụng cho tất cả
            .allowedOrigins("http://localhost:5173") // Cho phép React App của bạn
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
