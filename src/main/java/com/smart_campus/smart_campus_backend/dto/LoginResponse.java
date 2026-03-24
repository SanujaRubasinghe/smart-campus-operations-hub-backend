package com.smart_campus.smart_campus_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String refreshToken;
    private String type;
    private Long id;
    private String email;
    private String name;
    private String role;
    private String picture;
    private boolean mfaEnabled;
}
