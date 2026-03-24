package com.smart_campus.smart_campus_backend.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesRequest {
    private boolean emailBookings = true;
    private boolean emailTickets = true;
    private boolean emailComments = true;
    private boolean emailMarketing = false;
    private boolean inAppBookings = true;
    private boolean inAppTickets = true;
    private boolean inAppComments = true;
    private boolean inAppSystem = true;
    private boolean pushEnabled = false;
    private boolean soundEnabled = true;
    private boolean desktopNotifications = false;
    private boolean quietHoursEnabled = false;

    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format (HH:MM)")
    private String quietHoursStart = "22:00";

    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format (HH:MM)")
    private String quietHoursEnd = "08:00";
}