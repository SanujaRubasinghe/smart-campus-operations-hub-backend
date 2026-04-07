package com.springboot.smartcampusoperationshub.repository;

import com.springboot.smartcampusoperationshub.model.Booking;
import com.springboot.smartcampusoperationshub.model.User;
import com.springboot.smartcampusoperationshub.model.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT COUNT (b) FROM Booking b " +
            "WHERE b.resource.id = :resourceId " +
            "AND b.bookingDate = :bookingDate " +
            "AND b.status IN ('PENDING', 'APPROVED') " +
            "AND b.startTime < :endTime AND b.endTime > :startTime " +
            "AND (:excludeId IS NULL OR b.id <> :excludeId)"
    )
    long countConflicts(@Param("resourceId") UUID resourceId,
                       @Param("bookingDate")LocalDate bookingDate,
                       @Param("startTime")LocalTime startTime,
                       @Param("endTime")LocalTime endTime,
                       @Param("excludeId")Long excludeId);

    Page<Booking> findByUserId(Long userId, Pageable pageable);

    Page<Booking> findByUserIdAndStatus(Long userId, BookingStatus status,  Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE (:status IS NULL OR b.status = :status) " +
            "AND (:resourceId IS NULL OR b.resource.id = :resourceId) " +
            "AND (:userId IS NULL OR b.user.id = :userId) " +
            "AND (:bookingDate IS NULL OR b.bookingDate = :bookingDate)"
    )
    Page<Booking> findAllWithFilters(@Param("status")BookingStatus status,
                                     @Param("resourceId") UUID resourceId,
                                     @Param("userId")Long userId,
                                     @Param("bookingDate")LocalDate bookingDate,
                                     Pageable pageable);

    Long user(User user);
}