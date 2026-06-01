package ma.medisync.medisync_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AvailabilityRequest {
    private Long doctorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}