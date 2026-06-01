package ma.medisync.medisync_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Office;
import ma.medisync.medisync_backend.service.OfficeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/offices")
@RequiredArgsConstructor
@Tag(name = "Offices", description = "Office management API")
@SecurityRequirement(name = "JWT")
public class OfficeController {

    private final OfficeService officeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create office", description = "Create a new office/clinic location")
    public ResponseEntity<Office> createOffice(@RequestBody Office office) {
        Office createdOffice = officeService.createOffice(office);
        return new ResponseEntity<>(createdOffice, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get office by ID", description = "Retrieve a specific office")
    public ResponseEntity<Office> getOfficeById(@PathVariable Long id) {
        Optional<Office> office = officeService.getOfficeById(id);
        return office.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get all offices", description = "Retrieve list of all offices")
    public ResponseEntity<List<Office>> getAllOffices() {
        List<Office> offices = officeService.getAllOffices();
        return ResponseEntity.ok(offices);
    }

    @GetMapping("/city/{city}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get offices by city", description = "Retrieve offices in a specific city")
    public ResponseEntity<List<Office>> getOfficesByCity(@PathVariable String city) {
        List<Office> offices = officeService.getOfficesByCity(city);
        return ResponseEntity.ok(offices);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update office", description = "Update office information")
    public ResponseEntity<Office> updateOffice(@PathVariable Long id, @RequestBody Office officeDetails) {
        Office updatedOffice = officeService.updateOffice(id, officeDetails);
        if (updatedOffice != null) {
            return ResponseEntity.ok(updatedOffice);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete office", description = "Delete an office")
    public ResponseEntity<Void> deleteOffice(@PathVariable Long id) {
        officeService.deleteOffice(id);
        return ResponseEntity.noContent().build();
    }
}