package com.smart_campus.smart_campus_backend.repository;

import com.smart_campus.smart_campus_backend.model.NotificationPreferences;
import com.smart_campus.smart_campus_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {
    Optional<NotificationPreferences> findByUser(User user);

    Optional<NotificationPreferences> findByUserId(Long userId);
}