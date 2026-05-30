package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Patient;
import ma.medisync.medisync_backend.entity.User;
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
    private final UserService userService;

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
            p.setBloodType(patientDetails.getBloodType());
            p.setAllergies(patientDetails.getAllergies());
            p.setMedicalHistory(patientDetails.getMedicalHistory());
            p.setIsInsured(patientDetails.getIsInsured());
            p.setInsuranceCompany(patientDetails.getInsuranceCompany());
            p.setInsurancePolicyNumber(patientDetails.getInsurancePolicyNumber());
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

    public boolean existsByUserId(Long userId) {
        return patientRepository.existsByUserId(userId);
    }
}
