package com.springboot.smartcampusoperationshub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkAsReadRequest {
    private Long notificationId;
    private boolean all;
}