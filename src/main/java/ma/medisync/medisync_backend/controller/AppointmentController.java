package ma.medisync.medisync_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Appointment;
import ma.medisync.medisync_backend.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Create appointment", description = "Create a new appointment")
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        assertPatientCanAccess(appointment.getPatient() == null ? null : appointment.getPatient().getId());
        Appointment createdAppointment = appointmentService.createAppointment(appointment);
        return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get all appointments", description = "Retrieve all appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        if (isPatient()) {
            return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(currentUserId()));
        }
        List<Appointment> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get appointment by ID", description = "Retrieve a specific appointment")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);
        appointment.ifPresent(this::assertPatientCanAccess);
        return appointment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get appointments by patient", description = "Retrieve all appointments for a patient")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatient(@PathVariable Long patientId) {
        assertPatientCanAccess(patientId);
        List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get appointments by doctor", description = "Retrieve all appointments for a doctor")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(doctorId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get appointments by status", description = "Filter appointments by status")
    public ResponseEntity<List<Appointment>> getAppointmentsByStatus(@PathVariable String status) {
        List<Appointment> appointments = appointmentService.getAppointmentsByStatus(status);
        if (isPatient()) {
            appointments = appointments.stream()
                    .filter(appointment -> appointment.getPatient().getId().equals(currentUserId()))
                    .toList();
        }
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get upcoming appointments", description = "Retrieve upcoming appointments")
    public ResponseEntity<List<Appointment>> getUpcomingAppointments() {
        List<Appointment> appointments = appointmentService.getUpcomingAppointments();
        if (isPatient()) {
            appointments = appointments.stream()
                    .filter(appointment -> appointment.getPatient().getId().equals(currentUserId()))
                    .toList();
        }
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Update appointment", description = "Update an existing appointment")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody Appointment appointmentDetails) {
        appointmentService.getAppointmentById(id).ifPresent(this::assertPatientCanAccess);
        Appointment updatedAppointment = appointmentService.updateAppointment(id, appointmentDetails);
        if (updatedAppointment != null) {
            return ResponseEntity.ok(updatedAppointment);
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
    public ResponseEntity<Appointment> cancelAppointment(@PathVariable Long id) {
        appointmentService.getAppointmentById(id).ifPresent(this::assertPatientCanAccess);
        Appointment cancelledAppointment = appointmentService.cancelAppointment(id);
        if (cancelledAppointment != null) {
            return ResponseEntity.ok(cancelledAppointment);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/id/{id}/confirm")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Confirm appointment", description = "Confirm an appointment")
    public ResponseEntity<Appointment> confirmAppointment(@PathVariable Long id) {
        Appointment confirmedAppointment = appointmentService.confirmAppointment(id);
        if (confirmedAppointment != null) {
            return ResponseEntity.ok(confirmedAppointment);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/id/{id}/reschedule")
    @PreAuthorize("hasAnyRole('PATIENT', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Reschedule appointment", description = "Reschedule an appointment to a different date/time")
    public ResponseEntity<Appointment> rescheduleAppointment(@PathVariable Long id, @RequestBody Appointment appointmentDetails) {
        appointmentService.getAppointmentById(id).ifPresent(this::assertPatientCanAccess);
        Appointment rescheduled = appointmentService.rescheduleAppointment(id, appointmentDetails);
        if (rescheduled != null) {
            return ResponseEntity.ok(rescheduled);
        }
        return ResponseEntity.notFound().build();
    }

    private void assertPatientCanAccess(Appointment appointment) {
        assertPatientCanAccess(appointment.getPatient().getId());
    }

    private void assertPatientCanAccess(Long patientId) {
        if (isPatient() && (patientId == null || !patientId.equals(currentUserId()))) {
            throw new AccessDeniedException("Patients can only access their own appointments");
        }
    }

    private boolean isPatient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_PATIENT"));
    }

    private Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof ma.medisync.medisync_backend.entity.User user) {
            return user.getId();
        }
        throw new AccessDeniedException("Authenticated patient profile is required");
    }
}
