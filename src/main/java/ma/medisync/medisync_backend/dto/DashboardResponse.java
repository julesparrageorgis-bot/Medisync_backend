package ma.medisync.medisync_backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class DashboardResponse {
    private double occupancyRate;
    private double noShowRate;
    private BigDecimal totalRevenue;
    private long unpaidInvoices;
    private long totalAppointments;
    private Map<Long, Long> consultationsPerDoctor;
}
