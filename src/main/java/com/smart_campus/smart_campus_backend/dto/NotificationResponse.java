package com.smart_campus.smart_campus_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String type;
    private String title;
    private String message;
    private String link;
    private String imageUrl;
    private Map<String, Object> metadata;
    private boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}