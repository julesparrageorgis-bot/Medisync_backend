package ma.medisync.medisync_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.MedicalRecord;
import ma.medisync.medisync_backend.service.MedicalRecordService;
import ma.medisync.medisync_backend.service.SecurityService;
import ma.medisync.medisync_backend.service.ApiResponseMapper;
import ma.medisync.medisync_backend.dto.ApiResponses.MedicalRecordResponse;
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
    private final SecurityService securityService;
    private final ApiResponseMapper mapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "Create medical record", description = "Create a new medical record")
    public ResponseEntity<MedicalRecordResponse> createRecord(@RequestBody MedicalRecord record) {
        securityService.assertCanAccessMedicalData(record.getPatient().getId());
        securityService.assertDoctorSelf(record.getDoctor().getId());
        MedicalRecord createdRecord = medicalRecordService.createRecord(record);
        return new ResponseEntity<>(mapper.record(createdRecord), HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Get medical record by ID", description = "Retrieve a specific medical record")
    public ResponseEntity<MedicalRecordResponse> getRecordById(@PathVariable Long id) {
        Optional<MedicalRecord> record = medicalRecordService.getRecordById(id);
        record.ifPresent(value -> securityService.assertCanAccessMedicalData(value.getPatient().getId()));
        return record.map(mapper::record).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Get all medical records", description = "Retrieve all medical records")
    public ResponseEntity<List<MedicalRecordResponse>> getAllRecords() {
        List<MedicalRecord> records = medicalRecordService.getAllRecords().stream()
                .filter(record -> canAccessMedicalData(record.getPatient().getId()))
                .toList();
        return ResponseEntity.ok(records.stream().map(mapper::record).toList());
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Get patient medical records", description = "Retrieve all medical records for a patient")
    public ResponseEntity<List<MedicalRecordResponse>> getPatientRecords(@PathVariable Long patientId) {
        securityService.assertCanAccessMedicalData(patientId);
        List<MedicalRecord> records = medicalRecordService.getPatientRecords(patientId);
        return ResponseEntity.ok(records.stream().map(mapper::record).toList());
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "Get doctor medical records", description = "Retrieve all medical records created by a doctor")
    public ResponseEntity<List<MedicalRecordResponse>> getDoctorRecords(@PathVariable Long doctorId) {
        securityService.assertDoctorSelf(doctorId);
        List<MedicalRecord> records = medicalRecordService.getDoctorRecords(doctorId);
        return ResponseEntity.ok(records.stream().map(mapper::record).toList());
    }

    @GetMapping("/type/{recordType}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    @Operation(summary = "Get records by type", description = "Filter medical records by type")
    public ResponseEntity<List<MedicalRecordResponse>> getRecordsByType(@PathVariable String recordType) {
        List<MedicalRecord> records = medicalRecordService.getRecordsByType(recordType).stream()
                .filter(record -> canAccessMedicalData(record.getPatient().getId()))
                .toList();
        return ResponseEntity.ok(records.stream().map(mapper::record).toList());
    }

    @PutMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "Update medical record", description = "Update an existing medical record")
    public ResponseEntity<MedicalRecordResponse> updateRecord(@PathVariable Long id, @RequestBody MedicalRecord recordDetails) {
        medicalRecordService.getRecordById(id)
                .ifPresent(record -> securityService.assertCanAccessMedicalData(record.getPatient().getId()));
        MedicalRecord updatedRecord = medicalRecordService.updateRecord(id, recordDetails);
        if (updatedRecord != null) {
            return ResponseEntity.ok(mapper.record(updatedRecord));
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

    private boolean canAccessMedicalData(Long patientId) {
        try {
            securityService.assertCanAccessMedicalData(patientId);
            return true;
        } catch (org.springframework.security.access.AccessDeniedException ex) {
            return false;
        }
    }
}
