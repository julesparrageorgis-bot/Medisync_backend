package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.PerformedAct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PerformedActRepository extends JpaRepository<PerformedAct, Long> {
    List<PerformedAct> findByAppointmentId(Long appointmentId);
}