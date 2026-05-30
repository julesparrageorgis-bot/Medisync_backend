package ma.medisync.medisync_backend.controller;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Office;
import ma.medisync.medisync_backend.service.OfficeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/offices")
@RequiredArgsConstructor
public class OfficeController {

    private final OfficeService officeService;

    @PostMapping
    public ResponseEntity<Office> createOffice(@RequestBody Office office) {
        Office createdOffice = officeService.createOffice(office);
        return new ResponseEntity<>(createdOffice, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Office> getOfficeById(@PathVariable Long id) {
        Optional<Office> office = officeService.getOfficeById(id);
        return office.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Office>> getAllOffices() {
        List<Office> offices = officeService.getAllOffices();
        return ResponseEntity.ok(offices);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<Office>> getOfficesByCity(@PathVariable String city) {
        List<Office> offices = officeService.getOfficesByCity(city);
        return ResponseEntity.ok(offices);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Office> updateOffice(@PathVariable Long id, @RequestBody Office officeDetails) {
        Office updatedOffice = officeService.updateOffice(id, officeDetails);
        if (updatedOffice != null) {
            return ResponseEntity.ok(updatedOffice);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffice(@PathVariable Long id) {
        officeService.deleteOffice(id);
        return ResponseEntity.noContent().build();
    }
}