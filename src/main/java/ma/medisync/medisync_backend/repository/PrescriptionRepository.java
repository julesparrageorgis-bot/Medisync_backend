package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByPatientId(Long patientId);
    List<Prescription> findByDoctorId(Long doctorId);
    List<Prescription> findByStatus(String status);
    Optional<Prescription> findByPrescriptionNumber(String prescriptionNumber);
    List<Prescription> findByExpiryDateAfter(LocalDate date);
    List<Prescription> findByPatientIdAndStatus(Long patientId, String status);
}
