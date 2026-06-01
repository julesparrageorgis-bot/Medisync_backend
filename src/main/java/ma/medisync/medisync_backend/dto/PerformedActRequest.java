package ma.medisync.medisync_backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PerformedActRequest {
    private Long appointmentId;
    private String actCode;
    private String description;
    private BigDecimal amount;
}