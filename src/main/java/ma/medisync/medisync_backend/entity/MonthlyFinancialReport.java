package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Table(name = "monthly_financial_reports", uniqueConstraints = {
        @UniqueConstraint(name = "uk_monthly_financial_reports_month", columnNames = "report_month")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyFinancialReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_month", nullable = false, length = 7)
    private String reportMonth;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalRevenue;

    @Column(nullable = false)
    private Long unpaidInvoices;

    @Column(nullable = false)
    private Long totalAppointments;

    @Column(nullable = false)
    private Double roomOccupancyRate;

    @Column(nullable = false)
    private Double noShowRate;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    @PrePersist
    protected void onCreate() {
        if (reportMonth == null) {
            reportMonth = YearMonth.now().toString();
        }
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
    }
}
