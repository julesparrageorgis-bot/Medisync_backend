package com.medisync.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AvailabilityRequest {
    private Long doctorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}