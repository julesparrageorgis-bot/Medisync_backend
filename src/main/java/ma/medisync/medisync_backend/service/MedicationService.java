package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Medication;
import ma.medisync.medisync_backend.repository.MedicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicationService {

    private final MedicationRepository medicationRepository;

    public Medication createMedication(Medication medication) {
        return medicationRepository.save(medication);
    }

    public Optional<Medication> getMedicationById(Long id) {
        return medicationRepository.findById(id);
    }

    public List<Medication> getAllMedications() {
        return medicationRepository.findAll();
    }

    public Optional<Medication> getMedicationByName(String name) {
        return medicationRepository.findByName(name);
    }

    public List<Medication> getMedicationsByCategory(String category) {
        return medicationRepository.findAll().stream()
                .filter(med -> med.getForm() != null && med.getForm().equals(category))
                .toList();
    }

    public List<Medication> getLowStockMedications(Integer threshold) {
        return medicationRepository.findByStockQuantityLessThan(threshold);
    }

    public List<Medication> getExpiredMedications() {
        return medicationRepository.findAll().stream()
                .filter(med -> med.getIsActive())
                .toList();
    }

    public Medication updateMedication(Long id, Medication medicationDetails) {
        Optional<Medication> medication = medicationRepository.findById(id);
        if (medication.isPresent()) {
            Medication m = medication.get();
            m.setName(medicationDetails.getName());
            m.setForm(medicationDetails.getForm());
            m.setDosage(medicationDetails.getDosage());
            m.setPrice(medicationDetails.getPrice());
            m.setStockQuantity(medicationDetails.getStockQuantity());
            m.setManufacturer(medicationDetails.getManufacturer());
            m.setIsActive(medicationDetails.getIsActive());
            return medicationRepository.save(m);
        }
        return null;
    }

    public void updateStockQuantity(Long id, Integer newQuantity) {
        Optional<Medication> medication = medicationRepository.findById(id);
        if (medication.isPresent()) {
            Medication m = medication.get();
            m.setStockQuantity(newQuantity);
            medicationRepository.save(m);
        }
    }

    public void decrementStock(Long id, Integer quantity) {
        Optional<Medication> medication = medicationRepository.findById(id);
        if (medication.isPresent()) {
            Medication m = medication.get();
            Integer currentStock = m.getStockQuantity();
            if (currentStock >= quantity) {
                m.setStockQuantity(currentStock - quantity);
                medicationRepository.save(m);
            }
        }
    }

    public boolean deleteMedication(Long id) {
        if (medicationRepository.existsById(id)) {
            medicationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Medication> searchMedications(String searchTerm) {
        return medicationRepository.findAll().stream()
                .filter(med -> med.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        (med.getForm() != null && med.getForm().toLowerCase().contains(searchTerm.toLowerCase())))
                .toList();
    }
}
