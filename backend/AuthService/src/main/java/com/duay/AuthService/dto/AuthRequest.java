package com.duay.AuthService.dto;

public record AuthRequest( 
    String email,
    String password
) {}