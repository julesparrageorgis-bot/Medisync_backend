package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.MedicalReportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalReportTemplateRepository extends JpaRepository<MedicalReportTemplate, Long> {
}