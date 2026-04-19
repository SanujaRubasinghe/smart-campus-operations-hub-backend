package com.springboot.smartcampusoperationshub.util;

import com.springboot.smartcampusoperationshub.model.Booking;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TimeGapFinder {

    private static final LocalTime DAY_START = LocalTime.of(8, 0);
    private static final LocalTime DAY_END = LocalTime.of(20, 0);

    public List<TimeSlot> findFreeSlots(List<Booking> existingBookings,
                                        LocalTime preferredStart,
                                        Duration requiredDuration) {
        // Build a list of busy intervals, sorted
        List<TimeSlot> busy = new ArrayList<>();
        for (Booking b : existingBookings) {
            busy.add(new TimeSlot(b.getStartTime(), b.getEndTime()));
        }
        busy.sort(Comparator.comparing(TimeSlot::start));

        // Compute free intervals between busy ones
        List<TimeSlot> free = new ArrayList<>();
        LocalTime cursor = DAY_START;

        for (TimeSlot b : busy) {
            if (b.start().isAfter(cursor)) {
                free.add(new TimeSlot(cursor, b.start()));
            }
            if (b.end().isAfter(cursor)) {
                cursor = b.end();
            }
        }
        if (cursor.isBefore(DAY_END)) {
            free.add(new TimeSlot(cursor, DAY_END));
        }

        List<TimeSlot> candidates = new ArrayList<>();
        for (TimeSlot freeSlot : free) {
            long freeMinutes = ChronoUnit.MINUTES.between(freeSlot.start(), freeSlot.end());
            long needed = requiredDuration.toMinutes();
            if (freeMinutes < needed) continue;

            candidates.add(new TimeSlot(freeSlot.start(), freeSlot.start().plus(requiredDuration)));

            LocalTime alignedToEnd = freeSlot.end().minus(requiredDuration);
            if (!alignedToEnd.equals(freeSlot.start())) {
                candidates.add(new TimeSlot(alignedToEnd, freeSlot.end()));
            }
        }

        // Sort by proximity to preferred start time
        candidates.sort(Comparator.comparingLong(
                slot -> Math.abs(ChronoUnit.MINUTES.between(slot.start(), preferredStart))));

        return candidates;
    }

    public record TimeSlot(LocalTime start, LocalTime end) {}
}