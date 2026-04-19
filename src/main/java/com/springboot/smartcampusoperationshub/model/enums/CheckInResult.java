package com.springboot.smartcampusoperationshub.model.enums;

public enum CheckInResult {
    SUCCESS,
    ALREADY_CHECKED_IN,
    TOO_EARLY,
    GRACE_EXPIRED,
    BOOKING_CANCELLED,
    BOOKING_NO_SHOW,
    INVALID_TOKEN,
    LOCATION_MISMATCH
}