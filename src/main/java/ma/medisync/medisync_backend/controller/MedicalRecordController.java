package ma.medisync.medisync_backend.controller;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.MedicalRecord;
import ma.medisync.medisync_backend.service.MedicalRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    public ResponseEntity<MedicalRecord> createRecord(@RequestBody MedicalRecord record) {
        MedicalRecord createdRecord = medicalRecordService.createRecord(record);
        return new ResponseEntity<>(createdRecord, HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<MedicalRecord> getRecordById(@PathVariable Long id) {
        Optional<MedicalRecord> record = medicalRecordService.getRecordById(id);
        return record.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecord>> getAllRecords() {
        List<MedicalRecord> records = medicalRecordService.getAllRecords();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecord>> getPatientRecords(@PathVariable Long patientId) {
        List<MedicalRecord> records = medicalRecordService.getPatientRecords(patientId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<MedicalRecord>> getDoctorRecords(@PathVariable Long doctorId) {
        List<MedicalRecord> records = medicalRecordService.getDoctorRecords(doctorId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/type/{recordType}")
    public ResponseEntity<List<MedicalRecord>> getRecordsByType(@PathVariable String recordType) {
        List<MedicalRecord> records = medicalRecordService.getRecordsByType(recordType);
        return ResponseEntity.ok(records);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<MedicalRecord> updateRecord(@PathVariable Long id, @RequestBody MedicalRecord recordDetails) {
        MedicalRecord updatedRecord = medicalRecordService.updateRecord(id, recordDetails);
        if (updatedRecord != null) {
            return ResponseEntity.ok(updatedRecord);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        medicalRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
