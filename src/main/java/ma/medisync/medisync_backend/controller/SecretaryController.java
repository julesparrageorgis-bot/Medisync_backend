package ma.medisync.medisync_backend.controller;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Secretary;
import ma.medisync.medisync_backend.service.SecretaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/secretaries")
@RequiredArgsConstructor
public class SecretaryController {

    private final SecretaryService secretaryService;

    @PostMapping
    public ResponseEntity<Secretary> createSecretary(@RequestBody Secretary secretary) {
        Secretary createdSecretary = secretaryService.createSecretary(secretary);
        return new ResponseEntity<>(createdSecretary, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Secretary>> getAllSecretaries() {
        List<Secretary> secretaries = secretaryService.getAllSecretaries();
        return ResponseEntity.ok(secretaries);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Secretary> getSecretaryById(@PathVariable Long id) {
        Optional<Secretary> secretary = secretaryService.getSecretaryById(id);
        return secretary.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Secretary> getSecretaryByUserId(@PathVariable Long userId) {
        Optional<Secretary> secretary = secretaryService.getSecretaryByUserId(userId);
        return secretary.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/office/{officeAssigned}")
    public ResponseEntity<List<Secretary>> getSecretariesByOffice(@PathVariable String officeAssigned) {
        List<Secretary> secretaries = secretaryService.getSecretariesByOffice(officeAssigned);
        return ResponseEntity.ok(secretaries);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<Secretary> updateSecretary(@PathVariable Long id, @RequestBody Secretary secretaryDetails) {
        Secretary updatedSecretary = secretaryService.updateSecretary(id, secretaryDetails);
        if (updatedSecretary != null) {
            return ResponseEntity.ok(updatedSecretary);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteSecretary(@PathVariable Long id) {
        secretaryService.deleteSecretary(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/id/{id}/grant-appointment-permission")
    public ResponseEntity<Boolean> grantAppointmentPermission(@PathVariable Long id) {
        boolean result = secretaryService.grantAppointmentPermission(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/id/{id}/revoke-appointment-permission")
    public ResponseEntity<Boolean> revokeAppointmentPermission(@PathVariable Long id) {
        boolean result = secretaryService.revokeAppointmentPermission(id);
        return ResponseEntity.ok(result);
    }
}