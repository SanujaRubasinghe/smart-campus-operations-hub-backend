package com.springboot.smartcampusoperationshub.repository;

import com.springboot.smartcampusoperationshub.model.bookings.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.resourceId = :resourceId AND b.status IN ('PENDING','APPROVED') " +
            "AND ((b.startTime < :endTime AND b.endTime > :startTime))")
    boolean existsConflict(@Param("resourceId") Long resourceId,
                           @Param("startTime")LocalDateTime startTime,
                           @Param("endTime") LocalDateTime endTime);
}
