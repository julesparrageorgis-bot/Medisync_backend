package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Patient;
import ma.medisync.medisync_backend.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public Optional<Patient> getPatientByUserId(Long userId) {
        return patientRepository.findByUserId(userId);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient updatePatient(Long id, Patient patientDetails) {
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isPresent()) {
            Patient p = patient.get();
            if (patientDetails.getBloodType() != null) {
                p.setBloodType(patientDetails.getBloodType());
            }
            if (patientDetails.getAllergies() != null) {
                p.setAllergies(patientDetails.getAllergies());
            }
            if (patientDetails.getGender() != null) {
                p.setGender(patientDetails.getGender());
            }
            if (patientDetails.getDateOfBirth() != null) {
                p.setDateOfBirth(patientDetails.getDateOfBirth());
            }
            if (patientDetails.getAddress() != null) {
                p.setAddress(patientDetails.getAddress());
            }
            if (patientDetails.getCity() != null) {
                p.setCity(patientDetails.getCity());
            }
            if (patientDetails.getZipCode() != null) {
                p.setZipCode(patientDetails.getZipCode());
            }
            if (patientDetails.getEmergencyContact() != null) {
                p.setEmergencyContact(patientDetails.getEmergencyContact());
            }
            if (patientDetails.getEmergencyPhone() != null) {
                p.setEmergencyPhone(patientDetails.getEmergencyPhone());
            }
            return patientRepository.save(p);
        }
        return null;
    }

    public boolean deletePatient(Long id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
