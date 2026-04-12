package com.springboot.smartcampusoperationshub.service;

import com.springboot.smartcampusoperationshub.model.AuditLog;
import com.springboot.smartcampusoperationshub.model.User;
import com.springboot.smartcampusoperationshub.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(User user, String action, String module, String details, String ipAddress) {
        AuditLog log = new AuditLog();
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
