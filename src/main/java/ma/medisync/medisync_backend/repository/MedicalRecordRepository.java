package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientId(Long patientId);
    List<MedicalRecord> findByDoctorId(Long doctorId);
    List<MedicalRecord> findByRecordType(String recordType);
    List<MedicalRecord> findByRecordDateBetween(LocalDateTime start, LocalDateTime end);
    List<MedicalRecord> findByPatientIdOrderByRecordDateDesc(Long patientId);
}
