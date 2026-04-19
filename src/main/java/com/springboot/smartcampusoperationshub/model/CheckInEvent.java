package com.springboot.smartcampusoperationshub.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.springboot.smartcampusoperationshub.model.enums.CheckInResult;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "checkin_events", indexes = {
        @Index(name = "idx_checkin_events_booking", columnList = "booking_id"),
        @Index(name = "idx_checkin_events_scanned_at", columnList = "scannedAt")
})
public class CheckInEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = true)
    @JsonBackReference
    private Booking booking;

    @Column(nullable = false)
    private LocalDateTime scannedAt;

    @Column(length = 128)
    private String scannerDeviceId;

    @Column(length = 64)
    private String scannerIp;

    @Column(nullable = false)
    private Boolean locationVerified;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CheckInResult result;

    @Column(length = 255)
    private String resultMessage;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.locationVerified == null) {
            this.locationVerified = false;
        }
    }

    public CheckInEvent() {
    }

    public Long getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public LocalDateTime getScannedAt() {
        return scannedAt;
    }

    public void setScannedAt(LocalDateTime scannedAt) {
        this.scannedAt = scannedAt;
    }

    public String getScannerDeviceId() {
        return scannerDeviceId;
    }

    public void setScannerDeviceId(String scannerDeviceId) {
        this.scannerDeviceId = scannerDeviceId;
    }

    public String getScannerIp() {
        return scannerIp;
    }

    public void setScannerIp(String scannerIp) {
        this.scannerIp = scannerIp;
    }

    public Boolean getLocationVerified() {
        return locationVerified;
    }

    public void setLocationVerified(Boolean locationVerified) {
        this.locationVerified = locationVerified;
    }

    public CheckInResult getResult() {
        return result;
    }

    public void setResult(CheckInResult result) {
        this.result = result;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}