package com.springboot.smartcampusoperationshub.util;

import com.springboot.smartcampusoperationshub.dto.bookings.BookingRequestDTO;
import com.springboot.smartcampusoperationshub.model.Resource;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class AlternativeScorer {

    private static final double W_TYPE = 0.30;
    private static final double W_FLOOR = 0.20;
    private static final double W_CAPACITY = 0.15;
    private static final double W_AMENITY = 0.15;
    private static final double W_TIME = 0.15;
    private static final double W_POPULARITY = 0.05;

    public ScoredAlternative score(Resource originalResource,
                                   BookingRequestDTO originalRequest,
                                   Resource candidateResource,
                                   LocalTime candidateStartTime,
                                   LocalTime candidateEndTime) {
        List<String> reasons = new ArrayList<>();

        double typeScore = typeMatch(originalResource, candidateResource);
        double floorScore = floorProximity(originalResource, candidateResource);
        double capacityScore = capacityFit(originalRequest.getExpectedAttendees(), candidateResource.getCapacity());
        double timeScore = timeProximity(originalRequest.getStartTime(), candidateStartTime);
        double popularityScore = popularityBonus(candidateResource.getHistoricalPopularity());

        double composite = W_TYPE * typeScore
                + W_FLOOR * floorScore
                + W_CAPACITY * capacityScore
                + W_TIME * timeScore
                + W_POPULARITY * popularityScore;

        if (typeScore == 1.0) reasons.add("Same resource type");
        if (floorScore == 1.0) reasons.add("Same floor");
        else if (floorScore >= 0.5) reasons.add("Nearby floor");
        if (capacityScore >= 0.9) reasons.add("Capacity fits well");
        if (timeScore == 1.0) reasons.add("Same time slot");
        else if (timeScore >= 0.7) reasons.add("Close to requested time");

        return new ScoredAlternative(composite, reasons);
    }


    /** 1.0 if same type, 0.4 if reasonable substitute, 0.0 if poor match. */
    private double typeMatch(Resource original, Resource candidate) {
        if (original.getType() == candidate.getType()) return 1.0;
        return 0.4;
    }

    /** Same floor = 1.0, off-by-one = 0.6, off-by-two = 0.3, else 0.1. */
    private double floorProximity(Resource original, Resource candidate) {
        String origLoc = original.getLocation();
        String candLoc = candidate.getLocation();

        if (origLoc == null || candLoc == null) return 0.5;
        if (origLoc.equalsIgnoreCase(candLoc)) return 1.0;

        Integer origFloor = extractFloor(origLoc);
        Integer candFloor = extractFloor(candLoc);

        if (origFloor != null && candFloor != null) {
            int diff = Math.abs(origFloor - candFloor);
            return switch (diff) {
                case 0 -> 1.0;
                case 1 -> 0.6;
                case 2 -> 0.3;
                default -> 0.1;
            };
        }
        return 0.2;
    }

    private Integer extractFloor(String location) {
        if (location == null) return null;
        StringBuilder digits = new StringBuilder();
        for (char c : location.toCharArray()) {
            if (Character.isDigit(c)) digits.append(c);
        }
        if (digits.isEmpty()) return null;
        try {
            return Integer.parseInt(digits.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private double capacityFit(Integer requested, Integer available) {
        if (requested == null || available == null) return 0.5;
        if (available < requested) return 0.0; // can't fit, hard fail

        double ideal = requested * 1.2;
        double ratio = available / ideal;

        if (ratio <= 1.0) return 1.0;
        if (ratio <= 1.5) return 0.85;
        if (ratio <= 2.0) return 0.65;
        if (ratio <= 3.0) return 0.4;
        return 0.2; // wildly over-sized = wasteful
    }

    private Set<String> normalize(List<String> items) {
        if (items == null) return Collections.emptySet();
        Set<String> set = new HashSet<>();
        for (String s : items) {
            if (s != null) set.add(s.toLowerCase().trim());
        }
        return set;
    }

    private double timeProximity(LocalTime requested, LocalTime candidate) {
        long diffMinutes = Math.abs(ChronoUnit.MINUTES.between(requested, candidate));
        double maxWindow = 240.0; // 4 hours
        return Math.max(0.0, 1.0 - (diffMinutes / maxWindow));
    }

    private double popularityBonus(Integer popularity) {
        if (popularity == null || popularity <= 0) return 0.0;
        return Math.min(1.0, Math.log10(popularity + 1) / 3.0); // ~1.0 at 1000 bookings
    }

    public static class ScoredAlternative {
        public final double score;
        public final List<String> reasons;

        public ScoredAlternative(double score, List<String> reasons) {
            this.score = score;
            this.reasons = reasons;
        }
    }
}