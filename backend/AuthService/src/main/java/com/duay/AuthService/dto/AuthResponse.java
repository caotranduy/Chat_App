package com.duay.AuthService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private Integer authStatusCode;
    private String token;
}