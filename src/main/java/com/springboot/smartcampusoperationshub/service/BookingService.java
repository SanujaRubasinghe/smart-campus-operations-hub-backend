package com.springboot.smartcampusoperationshub.service;

import com.springboot.smartcampusoperationshub.dto.bookings.ApproveBookingDTO;
import com.springboot.smartcampusoperationshub.dto.bookings.BookingRequestDTO;
import com.springboot.smartcampusoperationshub.dto.bookings.RejectBookingDTO;
import com.springboot.smartcampusoperationshub.exception.ResourceNotFoundException;
import com.springboot.smartcampusoperationshub.exception.UserNotFoundException;
import com.springboot.smartcampusoperationshub.exception.bookings.BookingConflictException;
import com.springboot.smartcampusoperationshub.exception.bookings.BookingNotFoundException;
import com.springboot.smartcampusoperationshub.exception.bookings.InvalidStatusTransitionException;
import com.springboot.smartcampusoperationshub.model.Booking;
import com.springboot.smartcampusoperationshub.model.Resource;
import com.springboot.smartcampusoperationshub.model.User;
import com.springboot.smartcampusoperationshub.model.enums.BookingStatus;
import com.springboot.smartcampusoperationshub.repository.BookingRepository;
import com.springboot.smartcampusoperationshub.repository.ResourceRepository;
import com.springboot.smartcampusoperationshub.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          ResourceRepository resourceRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    public Booking createBooking(BookingRequestDTO dto, Long userId) {
        if(!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        Resource resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource Not Found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));
        long conflicts = bookingRepository.countConflicts(
                dto.getResourceId(),
                dto.getBookingDate(),
                dto.getStartTime(),
                dto.getEndTime(),
                null
        );

        if (conflicts > 0) {
            throw new BookingConflictException(
                    "The resource '" + resource.getName() + "' is already booked during the requested time range."
            );
        }

        Booking booking = new Booking();
        booking.setResource(resource);
        booking.setUser(user);
        booking.setBookingDate(dto.getBookingDate());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setPurpose(dto.getPurpose());
        booking.setExpectedAttendees(dto.getExpectedAttendees());
        booking.setStatus(BookingStatus.PENDING);

        return bookingRepository.save(booking);
    }

    public Booking approveBooking(Long bookingId, ApproveBookingDTO dto,  Long adminId) {
        Booking booking = findByIdOrThrow(bookingId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidStatusTransitionException(
                    "Only PENDING bookings can be approved. Current status: " + booking.getStatus()
            );
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException("User: " + adminId + " not found"));

        booking.setStatus(BookingStatus.APPROVED);
        booking.setAdminNote(dto.getNote());
        booking.setReviewedBy(admin);

        return bookingRepository.save(booking);
    }

    public Booking rejectBooking(Long bookingId, RejectBookingDTO dto,  Long adminId) {
        Booking booking = findByIdOrThrow(bookingId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidStatusTransitionException(
                    "Only PENDING bookings can be rejected. Current status: " + booking.getStatus()
            );
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException("User: " + adminId + " not found"));

        booking.setStatus(BookingStatus.REJECTED);
        booking.setAdminNote(dto.getReason());
        booking.setReviewedBy(admin);

        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(Long bookingId, Long requestingUserId) {
        Booking booking = findByIdOrThrow(bookingId);

        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new InvalidStatusTransitionException(
                    "Only APPROVED bookings can be cancelled. Current status: " + booking.getStatus()
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);

        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new BookingNotFoundException(bookingId);
        }
        bookingRepository.deleteById(bookingId);
    }

    @Transactional(readOnly = true)
    public Booking getBookingById(Long id) {
        return findByIdOrThrow(id);
    }

    @Transactional(readOnly = true)
    public Page<Booking> getMyBookings(Long userId, BookingStatus status, Pageable pageable) {
        if (status != null) {
            return bookingRepository.findByUserIdAndStatus(userId, status, pageable);
        }
        return bookingRepository.findByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Booking> getAllBookings(BookingStatus status, UUID resourceId, Long userId, LocalDate bookingDate,
                                        Pageable pageable) {
        return bookingRepository.findAllWithFilters(status, resourceId, userId, bookingDate, pageable);
    }

    private Booking findByIdOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }
}
