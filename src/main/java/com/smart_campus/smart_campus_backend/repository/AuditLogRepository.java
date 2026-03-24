package com.smart_campus.smart_campus_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public class AuditLogRepository {
    Page<AuditLog> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);
    Page<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<AuditLog> findByAction(String action, Pageable pageable);

}