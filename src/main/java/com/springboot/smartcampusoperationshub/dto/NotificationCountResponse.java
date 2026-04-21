package com.springboot.smartcampusoperationshub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCountResponse {
    private long unreadCount;
    private long totalCount;
}