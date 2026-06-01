package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TariffRepository extends JpaRepository<Tariff, Long> {
    List<Tariff> findByDoctorId(Long doctorId);
}