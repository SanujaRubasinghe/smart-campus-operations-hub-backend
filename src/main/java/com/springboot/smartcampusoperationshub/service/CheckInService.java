package com.springboot.smartcampusoperationshub.service;

import com.springboot.smartcampusoperationshub.dto.bookings.CheckInResponse;
import com.springboot.smartcampusoperationshub.model.Booking;
import com.springboot.smartcampusoperationshub.model.CheckInEvent;
import com.springboot.smartcampusoperationshub.model.Resource;
import com.springboot.smartcampusoperationshub.model.enums.BookingStatus;
import com.springboot.smartcampusoperationshub.model.enums.CheckInResult;
import com.springboot.smartcampusoperationshub.repository.BookingRepository;
import com.springboot.smartcampusoperationshub.repository.CheckInEventRepository;
import com.springboot.smartcampusoperationshub.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CheckInService {

    private final BookingRepository bookingRepo;
    private final ResourceRepository resourceRepository;
    private final CheckInEventRepository eventRepo;

    @Value("${app.checkin.grace-period-minutes:10}")
    private long gracePeriodMinutes;

    public CheckInService(BookingRepository bookingRepo,
                          ResourceRepository resourceRepository,
                          CheckInEventRepository eventRepo) {
        this.bookingRepo = bookingRepo;
        this.resourceRepository = resourceRepository;
        this.eventRepo = eventRepo;
    }

    @Transactional
    public CheckInResponse checkIn(Long userId, UUID resourceId, String providedSecret,
                                   String scannerIp) {
        Resource room = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        // verify whether the QR secret matches the resource
        if (providedSecret == null || !providedSecret.equals(room.getQrSecret())) {
            logEvent(null, scannerIp, false,
                    CheckInResult.INVALID_TOKEN, "QR secret mismatch");
            return fail(CheckInResult.INVALID_TOKEN, "Invalid QR code for this room");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime graceWindowStart = now.minusMinutes(gracePeriodMinutes);

        // check whether the QR has been scanned and checked in
        Optional<Booking> alreadyCheckedIn =
                bookingRepo.findActiveCheckedInBooking(userId, resourceId, now);
        if (alreadyCheckedIn.isPresent()) {
            Booking b = alreadyCheckedIn.get();
            logEvent(b, scannerIp, true,
                    CheckInResult.ALREADY_CHECKED_IN, "Idempotent scan");
            return success(b, "Already checked in at " + b.getCheckedInAt(),
                    CheckInResult.ALREADY_CHECKED_IN);
        }

        // find the user booking
        Optional<Booking> bookingOpt =
                bookingRepo.findActiveBookingForCheckIn(userId, resourceId, now, graceWindowStart);

        if (bookingOpt.isEmpty()) {
            // Figure out why: too early? no booking? already past grace?
            CheckInResult reason = diagnoseFailure(userId, resourceId, now);
            String msg = messageFor(reason);
            logEvent(null, scannerIp, false, reason, msg);
            return fail(reason, msg);
        }

        Booking booking = bookingOpt.get();

        booking.setStatus(BookingStatus.CHECKED_IN);
        booking.setCheckedInAt(now);
        bookingRepo.save(booking);

        logEvent(booking, scannerIp, true, CheckInResult.SUCCESS, "Checked in");

        return success(booking, "Checked in to " + booking.getResource().getName(),
                CheckInResult.SUCCESS);
    }

    private CheckInResult diagnoseFailure(Long userId, UUID resourceId, LocalDateTime now) {
        return CheckInResult.GRACE_EXPIRED;
    }

    private String messageFor(CheckInResult result) {
        return switch (result) {
            case TOO_EARLY -> "Your booking hasn't started yet";
            case GRACE_EXPIRED -> "No active booking found — check-in window expired";
            case BOOKING_CANCELLED -> "This booking was cancelled";
            case BOOKING_NO_SHOW -> "This booking was released due to no-show";
            default -> "Check-in failed";
        };
    }

    private void logEvent(Booking booking, String ip, boolean locationVerified,
                          CheckInResult result, String message) {
        CheckInEvent evt = new CheckInEvent();
        evt.setBooking(booking);
        evt.setScannedAt(LocalDateTime.now());
        evt.setScannerIp(ip);
        evt.setLocationVerified(locationVerified);
        evt.setResult(result);
        evt.setResultMessage(message);
        if (booking != null) {
            eventRepo.save(evt);
        }
    }

    private CheckInResponse success(Booking booking, String message, CheckInResult result) {
        CheckInResponse resp = new CheckInResponse(true, result, message);
        resp.setBookingId(booking.getId());
        resp.setResourceName(booking.getResource().getName());
        resp.setCheckedInAt(booking.getCheckedInAt());
        resp.setBookingEndTime(booking.getEndTime());
        return resp;
    }

    private CheckInResponse fail(CheckInResult result, String message) {
        return new CheckInResponse(false, result, message);
    }
}