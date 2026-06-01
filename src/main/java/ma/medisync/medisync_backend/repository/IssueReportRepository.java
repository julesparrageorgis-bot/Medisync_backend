package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.IssueReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueReportRepository extends JpaRepository<IssueReport, Long> {
}