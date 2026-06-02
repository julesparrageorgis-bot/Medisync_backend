package ma.medisync.medisync_backend.controller;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.*;
import ma.medisync.medisync_backend.repository.*;
import ma.medisync.medisync_backend.service.ConsultationRoomService;
import ma.medisync.medisync_backend.service.SecurityService;
import ma.medisync.medisync_backend.service.ApiResponseMapper;
import ma.medisync.medisync_backend.dto.ApiResponses.*;
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
    private final ApiResponseMapper mapper;

    @GetMapping("/rooms")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SECRETARY', 'ADMIN')")
    public List<ConsultationRoomResponse> rooms() {
        return roomService.getAllRooms().stream().map(mapper::room).toList();
    }

    @PostMapping("/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConsultationRoomResponse> createRoom(@RequestBody ConsultationRoom room) {
        return ResponseEntity.status(201).body(mapper.room(roomService.createRoom(room)));
    }

    @GetMapping("/equipment")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SECRETARY', 'ADMIN')")
    public List<EquipmentResponse> equipment() {
        return equipmentRepository.findAll().stream().map(mapper::equipment).toList();
    }

    @PostMapping("/equipment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponse> createEquipment(@RequestBody Equipment equipment) {
        return ResponseEntity.status(201).body(mapper.equipment(equipmentRepository.save(equipment)));
    }

    @GetMapping("/tariffs")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    public List<TariffResponse> tariffs() {
        return tariffRepository.findAll().stream().map(mapper::tariff).toList();
    }

    @PostMapping("/tariffs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TariffResponse> createTariff(@RequestBody Tariff tariff) {
        return ResponseEntity.status(201).body(mapper.tariff(tariffRepository.save(tariff)));
    }

    @GetMapping("/report-templates")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public List<MedicalReportTemplateResponse> templates() {
        return templateRepository.findAll().stream().map(mapper::template).toList();
    }

    @PostMapping("/report-templates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicalReportTemplateResponse> createTemplate(@RequestBody MedicalReportTemplate template) {
        return ResponseEntity.status(201).body(mapper.template(templateRepository.save(template)));
    }

    @GetMapping("/issues")
    @PreAuthorize("hasRole('ADMIN')")
    public List<IssueReportResponse> issues() {
        return issueRepository.findAll().stream().map(mapper::issue).toList();
    }

    @PostMapping("/issues")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<IssueReportResponse> reportIssue(@RequestBody IssueReport issue) {
        issue.setReporter(securityService.currentUser());
        issue.setStatus("OPEN");
        issue.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.status(201).body(mapper.issue(issueRepository.save(issue)));
    }

    @GetMapping("/dependents")
    @PreAuthorize("hasRole('PATIENT')")
    public List<DependentResponse> dependents() {
        return dependentRepository.findByParentPatientId(securityService.currentUser().getId()).stream()
                .map(mapper::dependent).toList();
    }

    @PostMapping("/dependents")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<DependentResponse> createDependent(@RequestBody Dependent dependent) {
        Patient parent = new Patient();
        parent.setId(securityService.currentUser().getId());
        dependent.setParentPatient(parent);
        return ResponseEntity.status(201).body(mapper.dependent(dependentRepository.save(dependent)));
    }

    @GetMapping("/doctor-unavailability/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SECRETARY', 'ADMIN')")
    public List<DoctorUnavailabilityResponse> unavailability(@PathVariable Long doctorId) {
        securityService.assertDoctorSelf(doctorId);
        return unavailabilityRepository.findByDoctorId(doctorId).stream().map(mapper::unavailability).toList();
    }

    @PostMapping("/doctor-unavailability")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<DoctorUnavailabilityResponse> createUnavailability(@RequestBody DoctorUnavailability value) {
        securityService.assertDoctorSelf(value.getDoctor().getId());
        if (value.getStartTime() == null || value.getEndTime() == null ||
                !value.getStartTime().isBefore(value.getEndTime())) {
            throw new ma.medisync.medisync_backend.exception.ValidationException("Invalid unavailability period");
        }
        return ResponseEntity.status(201).body(mapper.unavailability(unavailabilityRepository.save(value)));
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
