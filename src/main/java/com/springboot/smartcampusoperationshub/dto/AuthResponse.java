package com.springboot.smartcampusoperationshub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String type;
    private Long expiresIn;
    private UserProfileResponse user;
}