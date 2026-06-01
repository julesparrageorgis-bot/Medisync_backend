package ma.medisync.medisync_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.MedicalRecord;
import ma.medisync.medisync_backend.service.MedicalRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
@Tag(name = "Medical Records", description = "Medical record management API")
@SecurityRequirement(name = "JWT")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Create medical record", description = "Create a new medical record")
    public ResponseEntity<MedicalRecord> createRecord(@RequestBody MedicalRecord record) {
        MedicalRecord createdRecord = medicalRecordService.createRecord(record);
        return new ResponseEntity<>(createdRecord, HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get medical record by ID", description = "Retrieve a specific medical record")
    public ResponseEntity<MedicalRecord> getRecordById(@PathVariable Long id) {
        Optional<MedicalRecord> record = medicalRecordService.getRecordById(id);
        return record.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get all medical records", description = "Retrieve all medical records")
    public ResponseEntity<List<MedicalRecord>> getAllRecords() {
        List<MedicalRecord> records = medicalRecordService.getAllRecords();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get patient medical records", description = "Retrieve all medical records for a patient")
    public ResponseEntity<List<MedicalRecord>> getPatientRecords(@PathVariable Long patientId) {
        List<MedicalRecord> records = medicalRecordService.getPatientRecords(patientId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get doctor medical records", description = "Retrieve all medical records created by a doctor")
    public ResponseEntity<List<MedicalRecord>> getDoctorRecords(@PathVariable Long doctorId) {
        List<MedicalRecord> records = medicalRecordService.getDoctorRecords(doctorId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/type/{recordType}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get records by type", description = "Filter medical records by type")
    public ResponseEntity<List<MedicalRecord>> getRecordsByType(@PathVariable String recordType) {
        List<MedicalRecord> records = medicalRecordService.getRecordsByType(recordType);
        return ResponseEntity.ok(records);
    }

    @PutMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "Update medical record", description = "Update an existing medical record")
    public ResponseEntity<MedicalRecord> updateRecord(@PathVariable Long id, @RequestBody MedicalRecord recordDetails) {
        MedicalRecord updatedRecord = medicalRecordService.updateRecord(id, recordDetails);
        if (updatedRecord != null) {
            return ResponseEntity.ok(updatedRecord);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete medical record", description = "Delete a medical record")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        medicalRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
