package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.MedicalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicalDocumentRepository extends JpaRepository<MedicalDocument, Long> {
    List<MedicalDocument> findByPatientId(Long patientId);
    List<MedicalDocument> findByDocumentType(String documentType);
    List<MedicalDocument> findByUploadedById(Long userId);
    List<MedicalDocument> findByUploadDateBetween(LocalDateTime start, LocalDateTime end);
    List<MedicalDocument> findByPatientIdOrderByUploadDateDesc(Long patientId);
}
