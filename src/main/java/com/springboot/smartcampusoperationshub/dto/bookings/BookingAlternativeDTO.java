package com.springboot.smartcampusoperationshub.dto.bookings;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class BookingAlternativeDTO {
    private UUID resourceId;
    private String resourceName;
    private String resourceType;
    private String location;
    private Integer capacity;
    private List<String> amenities;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private double score;
    private List<String> reasons;
    private AlternativeStrategy strategy;

    public BookingAlternativeDTO() {}

    public enum AlternativeStrategy {
        SAME_RESOURCE_SHIFTED_TIME,
        SAME_TIME_DIFFERENT_RESOURCE,
        RELAXED_MATCH
    }

    // getters and setters
    public UUID getResourceId() { return resourceId; }
    public void setResourceId(UUID resourceId) { this.resourceId = resourceId; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }

    public AlternativeStrategy getStrategy() { return strategy; }
    public void setStrategy(AlternativeStrategy strategy) { this.strategy = strategy; }
}