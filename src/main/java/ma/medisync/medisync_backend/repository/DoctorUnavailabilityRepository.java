package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.DoctorUnavailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DoctorUnavailabilityRepository extends JpaRepository<DoctorUnavailability, Long> {
    List<DoctorUnavailability> findByDoctorId(Long doctorId);
    List<DoctorUnavailability> findByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long doctorId, LocalDateTime endTime, LocalDateTime startTime);
}
