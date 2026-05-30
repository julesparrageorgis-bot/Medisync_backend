package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {
    Optional<Medication> findByName(String name);
    List<Medication> findByForm(String form);
    List<Medication> findByManufacturer(String manufacturer);
    List<Medication> findByIsActiveTrue();
    List<Medication> findByStockQuantityLessThan(Integer quantity);
}
