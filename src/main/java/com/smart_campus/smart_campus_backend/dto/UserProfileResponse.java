package com.smart_campus.smart_campus_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String email;
    private String name;
    private String picture;
    private String role;
    private boolean mfaEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private NotificationPreferencesResponse notificationPreferences;
    private List<SessionResponse> activeSessions;
}