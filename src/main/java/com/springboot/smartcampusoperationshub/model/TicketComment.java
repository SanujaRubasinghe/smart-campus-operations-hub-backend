package com.springboot.smartcampusoperationshub.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_comments")
public class TicketComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String commentText;

    @Column(nullable = false)
    private String commenterName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole commenterRole;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    @JsonBackReference
    private IncidentTicket ticket;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public TicketComment() {
    }

    public Long getId() {
        return id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public IncidentTicket getTicket() {
        return ticket;
    }

    public void setTicket(IncidentTicket ticket) {
        this.ticket = ticket;
    }
}