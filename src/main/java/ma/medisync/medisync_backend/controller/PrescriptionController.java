package ma.medisync.medisync_backend.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Prescription;
import ma.medisync.medisync_backend.exception.ResourceNotFoundException;
import ma.medisync.medisync_backend.service.PrescriptionService;
import ma.medisync.medisync_backend.service.SecurityService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final SecurityService securityService;

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Prescription> create(@RequestBody Prescription prescription) {
        securityService.assertCanAccessMedicalData(prescription.getPatient().getId());
        securityService.assertDoctorSelf(prescription.getDoctor().getId());
        return ResponseEntity.status(201).body(prescriptionService.createPrescription(prescription));
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<Prescription> byId(@PathVariable Long id) {
        Prescription prescription = prescription(id);
        securityService.assertCanAccessMedicalData(prescription.getPatient().getId());
        return ResponseEntity.ok(prescription);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<List<Prescription>> byPatient(@PathVariable Long patientId) {
        securityService.assertCanAccessMedicalData(patientId);
        return ResponseEntity.ok(prescriptionService.getPatientPrescriptions(patientId));
    }

    @PutMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Prescription> update(@PathVariable Long id, @RequestBody Prescription details) {
        Prescription existing = prescription(id);
        securityService.assertCanAccessMedicalData(existing.getPatient().getId());
        return ResponseEntity.ok(prescriptionService.updatePrescription(id, details));
    }

    @DeleteMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/id/{id}/pdf")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) throws Exception {
        Prescription prescription = prescription(id);
        securityService.assertCanAccessMedicalData(prescription.getPatient().getId());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();
        document.add(new Paragraph("MediSync Prescription " + prescription.getPrescriptionNumber()));
        document.add(new Paragraph("Patient: " + prescription.getPatient().getFullName()));
        document.add(new Paragraph("Doctor: " + prescription.getDoctor().getFullName()));
        document.add(new Paragraph("Medication: " + prescription.getMedications()));
        document.add(new Paragraph("Instructions: " + String.valueOf(prescription.getInstructions())));
        document.close();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"prescription-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(output.toByteArray());
    }

    private Prescription prescription(Long id) {
        return prescriptionService.getPrescriptionById(id)
                .orElseThrow(() -> ResourceNotFoundException.prescriptionNotFound(id));
    }
}
