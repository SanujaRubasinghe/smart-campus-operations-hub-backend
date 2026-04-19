package com.springboot.smartcampusoperationshub.exception.bookings;

import com.springboot.smartcampusoperationshub.dto.bookings.BookingAlternativeDTO;

import java.util.Collections;
import java.util.List;

public class BookingConflictException extends RuntimeException {

    private final List<BookingAlternativeDTO> alternatives;

    public BookingConflictException(String message) {
        super(message);
        this.alternatives = Collections.emptyList();
    }

    public BookingConflictException(String message, List<BookingAlternativeDTO> alternatives) {
        super(message);
        this.alternatives = alternatives != null ? alternatives : Collections.emptyList();
    }

    public List<BookingAlternativeDTO> getAlternatives() {
        return alternatives;
    }
}