package ma.medisync.medisync_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AvailabilityResponse {
    private Long id;
    private Long doctorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean booked;
}