package com.springboot.smartcampusoperationshub.repository;

import com.springboot.smartcampusoperationshub.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {
    Optional<NotificationPreferences> findByUser(User user);

    Optional<NotificationPreferences> findByUserId(Long userId);
}