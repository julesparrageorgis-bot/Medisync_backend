package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.ClinicSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicSettingsRepository extends JpaRepository<ClinicSettings, Long> {
}