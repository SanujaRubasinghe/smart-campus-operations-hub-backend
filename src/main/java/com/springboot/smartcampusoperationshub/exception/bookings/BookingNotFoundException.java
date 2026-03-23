package com.springboot.smartcampusoperationshub.exception.bookings;

public class BookingNotFoundException extends RuntimeException {
  public BookingNotFoundException(String message) {
    super(message);
  }
}
