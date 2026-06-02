package ma.medisync.medisync_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Appointment;
import ma.medisync.medisync_backend.service.AppointmentService;
import ma.medisync.medisync_backend.service.SecurityService;
import ma.medisync.medisync_backend.entity.Dependent;
import ma.medisync.medisync_backend.repository.DependentRepository;
import ma.medisync.medisync_backend.service.ApiResponseMapper;
import ma.medisync.medisync_backend.dto.ApiResponses.AppointmentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Appointment management API")
@SecurityRequirement(name = "JWT")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final SecurityService securityService;
    private final DependentRepository dependentRepository;
    private final ApiResponseMapper mapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Create appointment", description = "Create a new appointment")
    public ResponseEntity<AppointmentResponse> createAppointment(@RequestBody Appointment appointment) {
        if (appointment.getPatient() != null) {
            securityService.assertCanAccessPatientProfile(appointment.getPatient().getId());
        }
        Appointment createdAppointment = appointmentService.createAppointment(appointment);
        return new ResponseEntity<>(mapper.appointment(createdAppointment), HttpStatus.CREATED);
    }

    @PostMapping("/emergency")
    @PreAuthorize("hasAnyRole('SECRETARY', 'ADMIN')")
    public ResponseEntity<AppointmentResponse> createEmergencyAppointment(@RequestBody Appointment appointment) {
        appointment.setEmergency(true);
        return new ResponseEntity<>(mapper.appointment(appointmentService.createAppointment(appointment)), HttpStatus.CREATED);
    }

    @PostMapping("/dependents/{dependentId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'SECRETARY', 'ADMIN')")
    public ResponseEntity<AppointmentResponse> createDependentAppointment(@PathVariable Long dependentId,
                                                                  @RequestBody Appointment appointment) {
        Dependent dependent = dependentRepository.findById(dependentId)
                .orElseThrow(() -> new ma.medisync.medisync_backend.exception.ResourceNotFoundException(
                        "Dependent not found with id: " + dependentId));
        securityService.assertCanAccessPatientProfile(dependent.getParentPatient().getId());
        appointment.setPatient(dependent.getParentPatient());
        appointment.setDependent(dependent);
        return new ResponseEntity<>(mapper.appointment(appointmentService.createAppointment(appointment)), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get all appointments", description = "Retrieve all appointments")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments().stream()
                .filter(securityService::canAccessAppointment)
                .toList();
        return ResponseEntity.ok(appointments.stream().map(mapper::appointment).toList());
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get appointment by ID", description = "Retrieve a specific appointment")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);
        appointment.ifPresent(securityService::assertCanAccessAppointment);
        return appointment.map(mapper::appointment).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get appointments by patient", description = "Retrieve all appointments for a patient")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByPatient(@PathVariable Long patientId) {
        securityService.assertCanAccessPatientProfile(patientId);
        List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(appointments.stream().map(mapper::appointment).toList());
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get appointments by doctor", description = "Retrieve all appointments for a doctor")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        securityService.assertDoctorSelf(doctorId);
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(doctorId);
        return ResponseEntity.ok(appointments.stream().map(mapper::appointment).toList());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get appointments by status", description = "Filter appointments by status")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByStatus(@PathVariable String status) {
        List<Appointment> appointments = appointmentService.getAppointmentsByStatus(status);
        appointments = appointments.stream().filter(securityService::canAccessAppointment).toList();
        return ResponseEntity.ok(appointments.stream().map(mapper::appointment).toList());
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get upcoming appointments", description = "Retrieve upcoming appointments")
    public ResponseEntity<List<AppointmentResponse>> getUpcomingAppointments() {
        List<Appointment> appointments = appointmentService.getUpcomingAppointments();
        appointments = appointments.stream().filter(securityService::canAccessAppointment).toList();
        return ResponseEntity.ok(appointments.stream().map(mapper::appointment).toList());
    }

    @PutMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Update appointment", description = "Update an existing appointment")
    public ResponseEntity<AppointmentResponse> updateAppointment(@PathVariable Long id, @RequestBody Appointment appointmentDetails) {
        appointmentService.getAppointmentById(id).ifPresent(securityService::assertCanAccessAppointment);
        Appointment updatedAppointment = appointmentService.updateAppointment(id, appointmentDetails);
        if (updatedAppointment != null) {
            return ResponseEntity.ok(mapper.appointment(updatedAppointment));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('SECRETARY', 'ADMIN')")
    @Operation(summary = "Delete appointment", description = "Delete an appointment")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/id/{id}/cancel")
    @PreAuthorize("hasAnyRole('PATIENT', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Cancel appointment", description = "Cancel an existing appointment")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable Long id) {
        appointmentService.getAppointmentById(id).ifPresent(securityService::assertCanAccessAppointment);
        Appointment cancelledAppointment = appointmentService.cancelAppointment(id);
        if (cancelledAppointment != null) {
            return ResponseEntity.ok(mapper.appointment(cancelledAppointment));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/id/{id}/confirm")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Confirm appointment", description = "Confirm an appointment")
    public ResponseEntity<AppointmentResponse> confirmAppointment(@PathVariable Long id) {
        Appointment confirmedAppointment = appointmentService.confirmAppointment(id);
        if (confirmedAppointment != null) {
            return ResponseEntity.ok(mapper.appointment(confirmedAppointment));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/id/{id}/reschedule")
    @PreAuthorize("hasAnyRole('PATIENT', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Reschedule appointment", description = "Reschedule an appointment to a different date/time")
    public ResponseEntity<AppointmentResponse> rescheduleAppointment(@PathVariable Long id, @RequestBody Appointment appointmentDetails) {
        appointmentService.getAppointmentById(id).ifPresent(securityService::assertCanAccessAppointment);
        Appointment rescheduled = appointmentService.rescheduleAppointment(id, appointmentDetails);
        if (rescheduled != null) {
            return ResponseEntity.ok(mapper.appointment(rescheduled));
        }
        return ResponseEntity.notFound().build();
    }

}
