package com.smartcampus.smart_campus_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String userEmail;

    private String action;

    private String resourceType;

    private Long resourceId;

    @Column(length = 2000)
    private String details;

    private String ipAddress;

    private String userAgent;

    private String status;

    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    public void setUser(User user) {
        if (user != null) {
            this.userId = user.getId();
            this.userEmail = user.getEmail();
        }
    }

    public void setModule(String module) {
        this.resourceType = module;
    }
}