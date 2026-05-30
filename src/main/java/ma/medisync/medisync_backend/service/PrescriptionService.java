package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Prescription;
import ma.medisync.medisync_backend.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public Prescription createPrescription(Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }

    public Optional<Prescription> getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id);
    }

    public Optional<Prescription> getPrescriptionByNumber(String number) {
        return prescriptionRepository.findByPrescriptionNumber(number);
    }

    public List<Prescription> getPatientPrescriptions(Long patientId) {
        return prescriptionRepository.findByPatientId(patientId);
    }

    public List<Prescription> getDoctorPrescriptions(Long doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId);
    }

    public List<Prescription> getPrescriptionsByStatus(String status) {
        return prescriptionRepository.findByStatus(status);
    }

    public List<Prescription> getActivePrescriptions(Long patientId) {
        return prescriptionRepository.findByPatientIdAndStatus(patientId, "ACTIVE");
    }

    public List<Prescription> getExpiredPrescriptions() {
        return prescriptionRepository.findAll().stream()
                .filter(p -> p.getExpiryDate() != null && p.getExpiryDate().isBefore(LocalDate.now()))
                .toList();
    }

    public Prescription updatePrescription(Long id, Prescription prescriptionDetails) {
        Optional<Prescription> prescription = prescriptionRepository.findById(id);
        if (prescription.isPresent()) {
            Prescription p = prescription.get();
            p.setMedications(prescriptionDetails.getMedications());
            p.setInstructions(prescriptionDetails.getInstructions());
            p.setStatus(prescriptionDetails.getStatus());
            return prescriptionRepository.save(p);
        }
        return null;
    }

    public boolean deletePrescription(Long id) {
        if (prescriptionRepository.existsById(id)) {
            prescriptionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
