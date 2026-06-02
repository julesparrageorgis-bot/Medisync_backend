package ma.medisync.medisync_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Doctor;
import ma.medisync.medisync_backend.service.DoctorService;
import ma.medisync.medisync_backend.service.SecurityService;
import ma.medisync.medisync_backend.service.ApiResponseMapper;
import ma.medisync.medisync_backend.dto.ApiResponses.DoctorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctors", description = "Doctor management API")
@SecurityRequirement(name = "JWT")
public class DoctorController {

    private final DoctorService doctorService;
    private final SecurityService securityService;
    private final ApiResponseMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create doctor", description = "Create a new doctor profile")
    public ResponseEntity<DoctorResponse> createDoctor(@RequestBody Doctor doctor) {
        Doctor createdDoctor = doctorService.createDoctor(doctor);
        return new ResponseEntity<>(mapper.doctor(createdDoctor), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get all doctors", description = "Retrieve list of all doctors")
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors.stream().map(mapper::doctor).toList());
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get doctor by ID", description = "Retrieve a specific doctor")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long id) {
        Optional<Doctor> doctor = doctorService.getDoctorById(id);
        return doctor.map(mapper::doctor).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/specialty/{specialty}")
    @PreAuthorize("hasAnyRole('PATIENT', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get doctors by specialty", description = "Find doctors by their specialty")
    public ResponseEntity<List<DoctorResponse>> getDoctorsBySpecialty(@PathVariable String specialty) {
        List<Doctor> doctors = doctorService.getDoctorsBySpecialty(specialty);
        return ResponseEntity.ok(doctors.stream().map(mapper::doctor).toList());
    }

    @GetMapping("/office/{officeId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get doctors by office", description = "Retrieve all doctors in an office")
    public ResponseEntity<List<DoctorResponse>> getDoctorsByOffice(@PathVariable Long officeId) {
        List<Doctor> doctors = doctorService.getDoctorsByOffice(officeId);
        return ResponseEntity.ok(doctors.stream().map(mapper::doctor).toList());
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('PATIENT', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get available doctors", description = "Retrieve available doctors for appointments")
    public ResponseEntity<List<DoctorResponse>> getAvailableDoctors() {
        List<Doctor> doctors = doctorService.getAvailableDoctors();
        return ResponseEntity.ok(doctors.stream().map(mapper::doctor).toList());
    }

    @PutMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "Update doctor", description = "Update doctor information")
    public ResponseEntity<DoctorResponse> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctorDetails) {
        securityService.assertDoctorSelf(id);
        Doctor updatedDoctor = doctorService.updateDoctor(id, doctorDetails);
        if (updatedDoctor != null) {
            return ResponseEntity.ok(mapper.doctor(updatedDoctor));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete doctor", description = "Delete a doctor profile")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}
