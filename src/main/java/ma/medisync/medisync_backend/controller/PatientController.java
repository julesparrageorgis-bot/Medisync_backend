package ma.medisync.medisync_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Patient;
import ma.medisync.medisync_backend.service.PatientService;
import ma.medisync.medisync_backend.service.SecurityService;
import ma.medisync.medisync_backend.service.ApiResponseMapper;
import ma.medisync.medisync_backend.dto.ApiResponses.PatientResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Patient management API")
@SecurityRequirement(name = "JWT")
public class PatientController {

    private final PatientService patientService;
    private final SecurityService securityService;
    private final ApiResponseMapper mapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    @Operation(summary = "Create patient", description = "Create a new patient profile")
    public ResponseEntity<PatientResponse> createPatient(@RequestBody Patient patient) {
        Patient createdPatient = patientService.createPatient(patient);
        return new ResponseEntity<>(mapper.patient(createdPatient), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get all patients", description = "Retrieve list of all patients")
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients.stream().map(mapper::patient).toList());
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get patient by ID", description = "Retrieve a specific patient")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {
        securityService.assertCanAccessPatientProfile(id);
        Optional<Patient> patient = patientService.getPatientById(id);
        return patient.map(mapper::patient).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get patient by user ID", description = "Retrieve patient information by user ID")
    public ResponseEntity<PatientResponse> getPatientByUserId(@PathVariable Long userId) {
        securityService.assertCanAccessPatientProfile(userId);
        Optional<Patient> patient = patientService.getPatientByUserId(userId);
        return patient.map(mapper::patient).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    @Operation(summary = "Update patient", description = "Update patient information")
    public ResponseEntity<PatientResponse> updatePatient(@PathVariable Long id, @RequestBody Patient patientDetails) {
        securityService.assertCanAccessPatientProfile(id);
        Patient updatedPatient = patientService.updatePatient(id, patientDetails);
        if (updatedPatient != null) {
            return ResponseEntity.ok(mapper.patient(updatedPatient));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete patient", description = "Delete a patient profile")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
