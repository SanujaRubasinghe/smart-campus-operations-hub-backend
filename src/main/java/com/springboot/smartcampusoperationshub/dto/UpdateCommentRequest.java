package com.springboot.smartcampusoperationshub.dto;

import com.springboot.smartcampusoperationshub.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateCommentRequest {

    @NotBlank(message = "Comment text is required")
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String commentText;

    @NotBlank(message = "Requester name is required")
    private String requesterName;

    @NotNull(message = "Requester role is required")
    private UserRole requesterRole;

    public UpdateCommentRequest() {
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public UserRole getRequesterRole() {
        return requesterRole;
    }

    public void setRequesterRole(UserRole requesterRole) {
        this.requesterRole = requesterRole;
    }
}