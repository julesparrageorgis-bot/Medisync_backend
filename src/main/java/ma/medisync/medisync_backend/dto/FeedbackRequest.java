package com.medisync.dto;

import lombok.Data;

@Data
public class FeedbackRequest {
    private Long appointmentId;
    private int rating; // 1-5
    private String comment;
}