package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
}