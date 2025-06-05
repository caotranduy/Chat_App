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
public class RegisterResponse {
    public enum ResultCode {
        SUCCESS, USERNAME_EXISTS, EMAIL_EXISTS, ERROR
    }    
    @JsonProperty("resultCode")
    private ResultCode resultCode;
    @JsonProperty("username")
    private String username;
    @JsonProperty("email")
    private String email;
}
