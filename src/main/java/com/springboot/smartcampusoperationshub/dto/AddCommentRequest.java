package com.springboot.smartcampusoperationshub.dto;

import com.springboot.smartcampusoperationshub.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AddCommentRequest {

    @NotBlank(message = "Comment text is required")
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String commentText;

    @NotBlank(message = "Commenter name is required")
    private String commenterName;

    // Optional — not required from frontend
    private UserRole commenterRole;

    public AddCommentRequest() {
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public UserRole getCommenterRole() {
        return commenterRole;
    }

    public void setCommenterRole(UserRole commenterRole) {
        this.commenterRole = commenterRole;
    }
}