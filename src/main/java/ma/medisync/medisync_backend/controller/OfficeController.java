package ma.medisync.medisync_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Office;
import ma.medisync.medisync_backend.dto.ApiResponses.OfficeResponse;
import ma.medisync.medisync_backend.service.ApiResponseMapper;
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
    private final ApiResponseMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create office", description = "Create a new office/clinic location")
    public ResponseEntity<OfficeResponse> createOffice(@RequestBody Office office) {
        Office createdOffice = officeService.createOffice(office);
        return new ResponseEntity<>(mapper.officeResponse(createdOffice), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get office by ID", description = "Retrieve a specific office")
    public ResponseEntity<OfficeResponse> getOfficeById(@PathVariable Long id) {
        Optional<Office> office = officeService.getOfficeById(id);
        return office.map(mapper::officeResponse).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get all offices", description = "Retrieve list of all offices")
    public ResponseEntity<List<OfficeResponse>> getAllOffices() {
        List<Office> offices = officeService.getAllOffices();
        return ResponseEntity.ok(offices.stream().map(mapper::officeResponse).toList());
    }

    @GetMapping("/city/{city}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Get offices by city", description = "Retrieve offices in a specific city")
    public ResponseEntity<List<OfficeResponse>> getOfficesByCity(@PathVariable String city) {
        List<Office> offices = officeService.getOfficesByCity(city);
        return ResponseEntity.ok(offices.stream().map(mapper::officeResponse).toList());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update office", description = "Update office information")
    public ResponseEntity<OfficeResponse> updateOffice(@PathVariable Long id, @RequestBody Office officeDetails) {
        Office updatedOffice = officeService.updateOffice(id, officeDetails);
        if (updatedOffice != null) {
            return ResponseEntity.ok(mapper.officeResponse(updatedOffice));
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
