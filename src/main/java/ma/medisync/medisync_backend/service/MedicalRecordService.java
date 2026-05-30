package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.MedicalRecord;
import ma.medisync.medisync_backend.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    public MedicalRecord createRecord(MedicalRecord record) {
        return medicalRecordRepository.save(record);
    }

    public Optional<MedicalRecord> getRecordById(Long id) {
        return medicalRecordRepository.findById(id);
    }

    public List<MedicalRecord> getPatientRecords(Long patientId) {
        return medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(patientId);
    }

    public List<MedicalRecord> getDoctorRecords(Long doctorId) {
        return medicalRecordRepository.findByDoctorId(doctorId);
    }

    public List<MedicalRecord> getRecordsByType(String recordType) {
        return medicalRecordRepository.findByRecordType(recordType);
    }

    public List<MedicalRecord> getRecordsByDateRange(LocalDateTime start, LocalDateTime end) {
        return medicalRecordRepository.findByRecordDateBetween(start, end);
    }

    public MedicalRecord updateRecord(Long id, MedicalRecord recordDetails) {
        Optional<MedicalRecord> record = medicalRecordRepository.findById(id);
        if (record.isPresent()) {
            MedicalRecord r = record.get();
            r.setDiagnosis(recordDetails.getDiagnosis());
            r.setSymptoms(recordDetails.getSymptoms());
            r.setTreatment(recordDetails.getTreatment());
            r.setNotes(recordDetails.getNotes());
            return medicalRecordRepository.save(r);
        }
        return null;
    }

    public boolean deleteRecord(Long id) {
        if (medicalRecordRepository.existsById(id)) {
            medicalRecordRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
