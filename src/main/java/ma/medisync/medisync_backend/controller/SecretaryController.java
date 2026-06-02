package ma.medisync.medisync_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Secretary;
import ma.medisync.medisync_backend.service.SecretaryService;
import ma.medisync.medisync_backend.service.ApiResponseMapper;
import ma.medisync.medisync_backend.dto.ApiResponses.SecretaryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/secretaries")
@RequiredArgsConstructor
@Tag(name = "Secretaries", description = "Secretary staff management API")
@SecurityRequirement(name = "JWT")
@PreAuthorize("hasAnyRole('SECRETARY', 'ADMIN')")
public class SecretaryController {

    private final SecretaryService secretaryService;
    private final ApiResponseMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create secretary", description = "Create a new secretary staff member")
    public ResponseEntity<SecretaryResponse> createSecretary(@RequestBody Secretary secretary) {
        Secretary createdSecretary = secretaryService.createSecretary(secretary);
        return new ResponseEntity<>(mapper.secretary(createdSecretary), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all secretaries", description = "Retrieve list of all secretaries")
    public ResponseEntity<List<SecretaryResponse>> getAllSecretaries() {
        List<Secretary> secretaries = secretaryService.getAllSecretaries();
        return ResponseEntity.ok(secretaries.stream().map(mapper::secretary).toList());
    }

    @GetMapping("/id/{id}")
    @Operation(summary = "Get secretary by ID", description = "Retrieve a specific secretary")
    public ResponseEntity<SecretaryResponse> getSecretaryById(@PathVariable Long id) {
        Optional<Secretary> secretary = secretaryService.getSecretaryById(id);
        return secretary.map(mapper::secretary).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get secretary by user ID", description = "Retrieve secretary information by user ID")
    public ResponseEntity<SecretaryResponse> getSecretaryByUserId(@PathVariable Long userId) {
        Optional<Secretary> secretary = secretaryService.getSecretaryByUserId(userId);
        return secretary.map(mapper::secretary).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/office/{officeAssigned}")
    @Operation(summary = "Get secretaries by office", description = "Retrieve all secretaries assigned to an office")
    public ResponseEntity<List<SecretaryResponse>> getSecretariesByOffice(@PathVariable String officeAssigned) {
        List<Secretary> secretaries = secretaryService.getSecretariesByOffice(officeAssigned);
        return ResponseEntity.ok(secretaries.stream().map(mapper::secretary).toList());
    }

    @PutMapping("/id/{id}")
    @Operation(summary = "Update secretary", description = "Update secretary information")
    public ResponseEntity<SecretaryResponse> updateSecretary(@PathVariable Long id, @RequestBody Secretary secretaryDetails) {
        Secretary updatedSecretary = secretaryService.updateSecretary(id, secretaryDetails);
        if (updatedSecretary != null) {
            return ResponseEntity.ok(mapper.secretary(updatedSecretary));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete secretary", description = "Delete a secretary profile")
    public ResponseEntity<Void> deleteSecretary(@PathVariable Long id) {
        secretaryService.deleteSecretary(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/id/{id}/grant-appointment-permission")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Grant appointment permission", description = "Grant secretary permission to manage appointments")
    public ResponseEntity<Boolean> grantAppointmentPermission(@PathVariable Long id) {
        boolean result = secretaryService.grantAppointmentPermission(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/id/{id}/revoke-appointment-permission")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Revoke appointment permission", description = "Revoke secretary appointment management permission")
    public ResponseEntity<Boolean> revokeAppointmentPermission(@PathVariable Long id) {
        boolean result = secretaryService.revokeAppointmentPermission(id);
        return ResponseEntity.ok(result);
    }
}
