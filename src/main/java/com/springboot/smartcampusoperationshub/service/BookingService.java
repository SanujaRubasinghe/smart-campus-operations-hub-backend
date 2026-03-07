package com.springboot.smartcampusoperationshub.service;

import com.springboot.smartcampusoperationshub.dto.BookingRequestDTO;
import com.springboot.smartcampusoperationshub.exception.BookingConflictException;
import com.springboot.smartcampusoperationshub.model.Booking;
import com.springboot.smartcampusoperationshub.model.BookingStatus;
import com.springboot.smartcampusoperationshub.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Booking createBooking(Long userId, BookingRequestDTO dto) {
        boolean conflict = bookingRepository.existsConflict(
                dto.getResourceId(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        if (conflict) {
            throw new BookingConflictException("Resource already booked for this time range.");
        }

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setResourceId(dto.getResourceId());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setPurpose(dto.getPurpose());
        booking.setAttendees(dto.getAttendees());
        booking.setStatus(BookingStatus.PENDING);

        return bookingRepository.save(booking);
    }
}
