package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.dto.ApiResponses.MonthlyFinancialReportResponse;
import ma.medisync.medisync_backend.dto.DashboardResponse;
import ma.medisync.medisync_backend.entity.MonthlyFinancialReport;
import ma.medisync.medisync_backend.repository.MonthlyFinancialReportRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MonthlyFinancialReportService {

    private final MonthlyFinancialReportRepository repository;
    private final DashboardService dashboardService;

    @Scheduled(cron = "0 5 0 1 * *")
    public void generateScheduledSnapshot() {
        generate(YearMonth.now().minusMonths(1));
    }

    public MonthlyFinancialReportResponse generate(YearMonth reportMonth) {
        DashboardResponse stats = dashboardService.getStats();
        MonthlyFinancialReport report = repository.findByReportMonth(reportMonth.toString())
                .orElseGet(MonthlyFinancialReport::new);
        report.setReportMonth(reportMonth.toString());
        report.setTotalRevenue(stats.getTotalRevenue());
        report.setUnpaidInvoices(stats.getUnpaidInvoices());
        report.setTotalAppointments(stats.getTotalAppointments());
        report.setRoomOccupancyRate(stats.getOccupancyRate());
        report.setNoShowRate(stats.getNoShowRate());
        report.setGeneratedAt(LocalDateTime.now());
        return response(repository.save(report));
    }

    @Transactional(readOnly = true)
    public List<MonthlyFinancialReportResponse> findAll() {
        return repository.findAllByOrderByReportMonthDesc().stream().map(this::response).toList();
    }

    @Transactional(readOnly = true)
    public MonthlyFinancialReportResponse findById(Long id) {
        return response(repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Monthly report not found: " + id)));
    }

    private MonthlyFinancialReportResponse response(MonthlyFinancialReport report) {
        return new MonthlyFinancialReportResponse(report.getId(), report.getReportMonth(),
                report.getTotalRevenue(), report.getUnpaidInvoices(), report.getTotalAppointments(),
                report.getRoomOccupancyRate(), report.getNoShowRate(), report.getGeneratedAt());
    }
}
