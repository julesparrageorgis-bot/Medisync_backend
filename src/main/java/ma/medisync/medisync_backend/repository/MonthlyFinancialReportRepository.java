package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.MonthlyFinancialReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MonthlyFinancialReportRepository extends JpaRepository<MonthlyFinancialReport, Long> {
    Optional<MonthlyFinancialReport> findByReportMonth(String reportMonth);
    List<MonthlyFinancialReport> findAllByOrderByReportMonthDesc();
}
