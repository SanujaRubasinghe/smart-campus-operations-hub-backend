package com.springboot.smartcampusoperationshub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesResponse {
    private boolean emailBookings;
    private boolean emailTickets;
    private boolean emailComments;
    private boolean emailMarketing;
    private boolean inAppBookings;
    private boolean inAppTickets;
    private boolean inAppComments;
    private boolean inAppSystem;
    private boolean pushEnabled;
    private boolean soundEnabled;
    private boolean desktopNotifications;
    private boolean quietHoursEnabled;
    private String quietHoursStart;
    private String quietHoursEnd;
}