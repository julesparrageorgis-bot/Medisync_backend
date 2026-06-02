package ma.medisync.medisync_backend.controller;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.*;
import ma.medisync.medisync_backend.repository.*;
import ma.medisync.medisync_backend.service.ConsultationRoomService;
import ma.medisync.medisync_backend.service.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/operations")
@RequiredArgsConstructor
public class OperationsController {

    private final ConsultationRoomService roomService;
    private final EquipmentRepository equipmentRepository;
    private final TariffRepository tariffRepository;
    private final MedicalReportTemplateRepository templateRepository;
    private final IssueReportRepository issueRepository;
    private final DependentRepository dependentRepository;
    private final DoctorUnavailabilityRepository unavailabilityRepository;
    private final SecurityService securityService;

    @GetMapping("/rooms")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SECRETARY', 'ADMIN')")
    public List<ConsultationRoom> rooms() { return roomService.getAllRooms(); }

    @PostMapping("/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConsultationRoom> createRoom(@RequestBody ConsultationRoom room) {
        return ResponseEntity.status(201).body(roomService.createRoom(room));
    }

    @GetMapping("/equipment")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SECRETARY', 'ADMIN')")
    public List<Equipment> equipment() { return equipmentRepository.findAll(); }

    @PostMapping("/equipment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Equipment> createEquipment(@RequestBody Equipment equipment) {
        return ResponseEntity.status(201).body(equipmentRepository.save(equipment));
    }

    @GetMapping("/tariffs")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    public List<Tariff> tariffs() { return tariffRepository.findAll(); }

    @PostMapping("/tariffs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tariff> createTariff(@RequestBody Tariff tariff) {
        return ResponseEntity.status(201).body(tariffRepository.save(tariff));
    }

    @GetMapping("/report-templates")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public List<MedicalReportTemplate> templates() { return templateRepository.findAll(); }

    @PostMapping("/report-templates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicalReportTemplate> createTemplate(@RequestBody MedicalReportTemplate template) {
        return ResponseEntity.status(201).body(templateRepository.save(template));
    }

    @GetMapping("/issues")
    @PreAuthorize("hasRole('ADMIN')")
    public List<IssueReport> issues() { return issueRepository.findAll(); }

    @PostMapping("/issues")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<IssueReport> reportIssue(@RequestBody IssueReport issue) {
        issue.setReporter(securityService.currentUser());
        issue.setStatus("OPEN");
        issue.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.status(201).body(issueRepository.save(issue));
    }

    @GetMapping("/dependents")
    @PreAuthorize("hasRole('PATIENT')")
    public List<Dependent> dependents() {
        return dependentRepository.findByParentPatientId(securityService.currentUser().getId());
    }

    @PostMapping("/dependents")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Dependent> createDependent(@RequestBody Dependent dependent) {
        Patient parent = new Patient();
        parent.setId(securityService.currentUser().getId());
        dependent.setParentPatient(parent);
        return ResponseEntity.status(201).body(dependentRepository.save(dependent));
    }

    @GetMapping("/doctor-unavailability/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SECRETARY', 'ADMIN')")
    public List<DoctorUnavailability> unavailability(@PathVariable Long doctorId) {
        securityService.assertDoctorSelf(doctorId);
        return unavailabilityRepository.findByDoctorId(doctorId);
    }

    @PostMapping("/doctor-unavailability")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<DoctorUnavailability> createUnavailability(@RequestBody DoctorUnavailability value) {
        securityService.assertDoctorSelf(value.getDoctor().getId());
        if (value.getStartTime() == null || value.getEndTime() == null ||
                !value.getStartTime().isBefore(value.getEndTime())) {
            throw new ma.medisync.medisync_backend.exception.ValidationException("Invalid unavailability period");
        }
        return ResponseEntity.status(201).body(unavailabilityRepository.save(value));
    }

    @DeleteMapping("/doctor-unavailability/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteUnavailability(@PathVariable Long id) {
        DoctorUnavailability value = unavailabilityRepository.findById(id).orElseThrow();
        securityService.assertDoctorSelf(value.getDoctor().getId());
        unavailabilityRepository.delete(value);
        return ResponseEntity.noContent().build();
    }
}
