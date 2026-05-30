package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.MedicalDocument;
import ma.medisync.medisync_backend.repository.MedicalDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicalDocumentService {

    private final MedicalDocumentRepository medicalDocumentRepository;

    public MedicalDocument uploadDocument(MedicalDocument document) {
        document.setUploadDate(LocalDateTime.now());
        return medicalDocumentRepository.save(document);
    }

    public Optional<MedicalDocument> getDocumentById(Long id) {
        return medicalDocumentRepository.findById(id);
    }

    public List<MedicalDocument> getPatientDocuments(Long patientId) {
        return medicalDocumentRepository.findByPatientIdOrderByUploadDateDesc(patientId);
    }

    public List<MedicalDocument> getPatientDocumentsByType(Long patientId, String documentType) {
        return medicalDocumentRepository.findAll().stream()
                .filter(doc -> doc.getPatient().getId().equals(patientId) && 
                        doc.getDocumentType().equals(documentType))
                .toList();
    }

    public List<MedicalDocument> getDocumentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return medicalDocumentRepository.findAll().stream()
                .filter(doc -> !doc.getUploadDate().isBefore(startDate) && !doc.getUploadDate().isAfter(endDate))
                .toList();
    }

    public List<MedicalDocument> getDocumentsByType(String documentType) {
        return medicalDocumentRepository.findAll().stream()
                .filter(doc -> doc.getDocumentType().equals(documentType))
                .toList();
    }

    public MedicalDocument updateDocument(Long id, MedicalDocument documentDetails) {
        Optional<MedicalDocument> document = medicalDocumentRepository.findById(id);
        if (document.isPresent()) {
            MedicalDocument doc = document.get();
            doc.setFileName(documentDetails.getFileName());
            doc.setDocumentType(documentDetails.getDocumentType());
            doc.setDescription(documentDetails.getDescription());
            doc.setFileSize(documentDetails.getFileSize());
            return medicalDocumentRepository.save(doc);
        }
        return null;
    }

    public boolean deleteDocument(Long id) {
        if (medicalDocumentRepository.existsById(id)) {
            medicalDocumentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<MedicalDocument> searchDocuments(Long patientId, String searchTerm) {
        return medicalDocumentRepository.findByPatientIdOrderByUploadDateDesc(patientId).stream()
                .filter(doc -> doc.getFileName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        (doc.getDescription() != null && doc.getDescription().toLowerCase().contains(searchTerm.toLowerCase())))
                .toList();
    }

    public List<MedicalDocument> getLargeFiles(Long fileSizeThreshold) {
        return medicalDocumentRepository.findAll().stream()
                .filter(doc -> doc.getFileSize() > fileSizeThreshold)
                .toList();
    }
}
