package com.smart_campus.smart_campus_backend.service;

import com.smart_campus.smart_campus_backend.model.User;
import com.smart_campus.smart_campus_backend.repository.AuditLogRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(User user, String action, String module, String details, String ipAddress) {
        com.smart_campus.smart_campus_backend.model.AuditLog log = new com.smart_campus.smart_campus_backend.model.AuditLog();
        log.setUser(user);
        log.setAction(action);
        log.setModule(module);
        log.setDetails(details);
        log.setIpAddress(ipAddress);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    public void logSystemAction(String action, String module, String details) {
        log(null, action, module, details, "SYSTEM");
    }
}
