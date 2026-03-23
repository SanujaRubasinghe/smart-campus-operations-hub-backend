package com.springboot.smartcampusoperationshub.exception.bookings;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long id) {
        super("Booking not found with id: " + id);
    }
}
