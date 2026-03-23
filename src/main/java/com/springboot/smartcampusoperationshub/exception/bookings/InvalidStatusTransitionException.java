package com.springboot.smartcampusoperationshub.exception.bookings;

public class InvalidStatusTransitionException extends RuntimeException {
  public InvalidStatusTransitionException(String message) {
    super(message);
  }
}
