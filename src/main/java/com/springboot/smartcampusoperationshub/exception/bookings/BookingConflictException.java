package com.springboot.smartcampusoperationshub.exception.bookings;

public class BookingConflictException extends RuntimeException {
    public BookingConflictException(String message) {
        super(message);
    }
}
