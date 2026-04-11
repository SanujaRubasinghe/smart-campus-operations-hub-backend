package com.springboot.smartcampusoperationshub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(nullable = false)
    private boolean emailBookings = true;

    @Column(nullable = false)
    private boolean emailTickets = true;

    @Column(nullable = false)
    private boolean emailComments = true;

    @Column(nullable = false)
    private boolean emailMarketing = false;

    @Column(nullable = false)
    private boolean inAppBookings = true;

    @Column(nullable = false)
    private boolean inAppTickets = true;

    @Column(nullable = false)
    private boolean inAppComments = true;

    @Column(nullable = false)
    private boolean inAppSystem = true;

    @Column(nullable = false)
    private boolean pushEnabled = false;

    @Column(nullable = false)
    private boolean soundEnabled = true;

    @Column(nullable = false)
    private boolean desktopNotifications = false;

    @Column(nullable = false)
    private boolean quietHoursEnabled = false;

    private String quietHoursStart = "22:00";

    private String quietHoursEnd = "08:00";
}