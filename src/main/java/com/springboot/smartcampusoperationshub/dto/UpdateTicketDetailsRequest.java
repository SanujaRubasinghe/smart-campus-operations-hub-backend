package com.springboot.smartcampusoperationshub.dto;

import com.springboot.smartcampusoperationshub.model.TicketCategory;
import com.springboot.smartcampusoperationshub.model.TicketPriority;

public class UpdateTicketDetailsRequest {

    private TicketCategory category;
    private String location;
    private String description;
    private TicketPriority priority;
    private String preferredContact;

    public TicketCategory getCategory() {
        return category;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public String getPreferredContact() {
        return preferredContact;
    }

    public void setPreferredContact(String preferredContact) {
        this.preferredContact = preferredContact;
    }
}