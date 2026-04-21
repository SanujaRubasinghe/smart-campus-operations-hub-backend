package com.springboot.smartcampusoperationshub.dto.bookings;

public class CheckInRequest {
    private String qrSecret;

    public CheckInRequest() {}

    public String getQrSecret() {
        return qrSecret;
    }

    public void setQrSecret(String qrSecret) {
        this.qrSecret = qrSecret;
    }
}