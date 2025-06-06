package com.duay.AuthService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    @JsonProperty("access_token") 
    private String accessToken;
    @JsonProperty("refresh_token") 
    private String refreshToken;
}

// public class AuthenticationResponse {
//     @JsonProperty("user")
//     private String user;
// }