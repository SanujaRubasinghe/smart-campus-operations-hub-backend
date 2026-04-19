package com.springboot.smartcampusoperationshub.dto.bookings;

import jakarta.validation.constraints.Size;

public class ApproveBookingDTO {
    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;

    public ApproveBookingDTO() {}

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
