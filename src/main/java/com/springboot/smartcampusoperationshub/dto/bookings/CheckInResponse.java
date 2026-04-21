package com.springboot.smartcampusoperationshub.dto.bookings;

import com.springboot.smartcampusoperationshub.model.enums.CheckInResult;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class CheckInResponse {
    private boolean success;
    private CheckInResult result;
    private String message;
    private Long bookingId;
    private String resourceName;
    private LocalDateTime checkedInAt;
    private LocalTime bookingEndTime;

    public CheckInResponse() {}

    public CheckInResponse(boolean success, CheckInResult result, String message) {
        this.success = success;
        this.result = result;
        this.message = message;
    }

    // getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public CheckInResult getResult() { return result; }
    public void setResult(CheckInResult result) { this.result = result; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public LocalDateTime getCheckedInAt() { return checkedInAt; }
    public void setCheckedInAt(LocalDateTime checkedInAt) { this.checkedInAt = checkedInAt; }

    public LocalTime getBookingEndTime() { return bookingEndTime; }
    public void setBookingEndTime(LocalTime bookingEndTime) { this.bookingEndTime = bookingEndTime; }
}