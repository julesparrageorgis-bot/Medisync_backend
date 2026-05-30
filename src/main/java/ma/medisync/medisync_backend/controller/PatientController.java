package ma.medisync.medisync_backend.controller;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.dto.PatientDTO;
import ma.medisync.medisync_backend.entity.Patient;
import ma.medisync.medisync_backend.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody PatientDTO patientDTO) {
        Patient patient = Patient.builder()
                .bloodType(patientDTO.getBloodType())
                .allergies(patientDTO.getAllergies())
                .medicalHistory(patientDTO.getMedicalHistory())
                .insuranceCompany(patientDTO.getInsuranceCompany())
                .insurancePolicyNumber(patientDTO.getInsurancePolicyNumber())
                .isInsured(true)
                .build();
        
        Patient createdPatient = patientService.createPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        Optional<Patient> patient = patientService.getPatientById(id);
        return patient.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Patient> getPatientByUserId(@PathVariable Long userId) {
        Optional<Patient> patient = patientService.getPatientByUserId(userId);
        return patient.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientDTO patientDTO) {
        Patient patientDetails = Patient.builder()
                .bloodType(patientDTO.getBloodType())
                .allergies(patientDTO.getAllergies())
                .medicalHistory(patientDTO.getMedicalHistory())
                .insuranceCompany(patientDTO.getInsuranceCompany())
                .insurancePolicyNumber(patientDTO.getInsurancePolicyNumber())
                .build();
        
        Patient updatedPatient = patientService.updatePatient(id, patientDetails);
        if (updatedPatient != null) {
            return ResponseEntity.ok(updatedPatient);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        if (patientService.deletePatient(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{userId}/exists")
    public ResponseEntity<Boolean> existsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(patientService.existsByUserId(userId));
    }
}
