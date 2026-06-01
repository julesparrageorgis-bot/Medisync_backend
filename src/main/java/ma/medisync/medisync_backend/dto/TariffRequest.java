package ma.medisync.medisync_backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TariffRequest {
    private Long doctorId;
    private String actType;
    private String sector; // 1,2,3
    private BigDecimal price;
}