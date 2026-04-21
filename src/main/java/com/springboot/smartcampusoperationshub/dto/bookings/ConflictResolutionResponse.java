package com.springboot.smartcampusoperationshub.dto.bookings;

import java.util.List;

public class ConflictResolutionResponse {
    private boolean conflict;
    private String conflictReason;
    private List<BookingAlternativeDTO> alternatives;

    public ConflictResolutionResponse() {}

    public ConflictResolutionResponse(boolean conflict, String conflictReason,
                                      List<BookingAlternativeDTO> alternatives) {
        this.conflict = conflict;
        this.conflictReason = conflictReason;
        this.alternatives = alternatives;
    }

    public boolean isConflict() { return conflict; }
    public void setConflict(boolean conflict) { this.conflict = conflict; }

    public String getConflictReason() { return conflictReason; }
    public void setConflictReason(String conflictReason) { this.conflictReason = conflictReason; }

    public List<BookingAlternativeDTO> getAlternatives() { return alternatives; }
    public void setAlternatives(List<BookingAlternativeDTO> alternatives) { this.alternatives = alternatives; }
}