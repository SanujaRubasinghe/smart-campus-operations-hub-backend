package com.springboot.smartcampusoperationshub.dto.bookings;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingRequestDTO {
    @NotNull(message = "Resource ID is required")
    private Long ResourceId;

    @NotNull(message = "Booking date is required")
    @FutureOrPresent(message = "Booking date must not be in the past")
    private LocalDate bookingDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotBlank(message = "Purpose is required")
    @Size(max = 1000, message = "Purpose must not exceed 1000 characters")
    private String purpose;

    @Min(value = 1, message = "Expected attendees must be at least 1")
    private Integer expectedAttendees;

    public BookingRequestDTO() {}

    public Long getResourceId() {
        return ResourceId;
    }

    public void setResourceId(Long resourceId) {
        ResourceId = resourceId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Integer getExpectedAttendees() {
        return expectedAttendees;
    }

    public void setExpectedAttendees(Integer expectedAttendees) {
        this.expectedAttendees = expectedAttendees;
    }
}

