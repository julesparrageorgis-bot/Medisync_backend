package ma.medisync.medisync_backend.controller;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.dto.ApiResponses.MedicationResponse;
import ma.medisync.medisync_backend.entity.Medication;
import ma.medisync.medisync_backend.service.ApiResponseMapper;
import ma.medisync.medisync_backend.service.MedicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService medicationService;
    private final ApiResponseMapper mapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public List<MedicationResponse> all() {
        return medicationService.getAllMedications().stream().map(mapper::medication).toList();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public List<MedicationResponse> search(@RequestParam String query) {
        return medicationService.searchMedications(query).stream().map(mapper::medication).toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicationResponse> create(@RequestBody Medication medication) {
        return ResponseEntity.status(201).body(mapper.medication(medicationService.createMedication(medication)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicationResponse> update(@PathVariable Long id, @RequestBody Medication medication) {
        return ResponseEntity.ok(mapper.medication(medicationService.updateMedication(id, medication)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicationService.deleteMedication(id);
        return ResponseEntity.noContent().build();
    }
}
