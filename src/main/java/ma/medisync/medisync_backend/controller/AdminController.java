package ma.medisync.medisync_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Admin;
import ma.medisync.medisync_backend.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Admin user management API")
@SecurityRequirement(name = "JWT")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    @Operation(summary = "Create admin", description = "Create a new admin user")
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        Admin createdAdmin = adminService.createAdmin(admin);
        return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all admins", description = "Retrieve list of all admin users")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        List<Admin> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/id/{id}")
    @Operation(summary = "Get admin by ID", description = "Retrieve a specific admin by ID")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
        Optional<Admin> admin = adminService.getAdminById(id);
        return admin.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/id/{id}")
    @Operation(summary = "Update admin", description = "Update an existing admin")
    public ResponseEntity<Admin> updateAdmin(@PathVariable Long id, @RequestBody Admin adminDetails) {
        Admin updatedAdmin = adminService.updateAdmin(id, adminDetails);
        if (updatedAdmin != null) {
            return ResponseEntity.ok(updatedAdmin);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/id/{id}")
    @Operation(summary = "Delete admin", description = "Delete an admin user")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/id/{id}/role-permission/{permission}")
    @Operation(summary = "Grant permission", description = "Grant a specific permission to an admin")
    public ResponseEntity<Boolean> grantPermission(@PathVariable Long id, @PathVariable String permission) {
        boolean result = adminService.grantPermission(id, permission);
        return ResponseEntity.ok(result);
    }
}