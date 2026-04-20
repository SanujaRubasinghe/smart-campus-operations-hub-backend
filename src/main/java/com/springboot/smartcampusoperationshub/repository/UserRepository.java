package com.springboot.smartcampusoperationshub.repository;

import com.springboot.smartcampusoperationshub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderId(String providerId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.notificationPreferences WHERE u.email = :email")
    Optional<User> findByEmailWithPreferences(@Param("email") String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.role = 'ADMIN'")
    boolean hasAnyAdmin();
}
