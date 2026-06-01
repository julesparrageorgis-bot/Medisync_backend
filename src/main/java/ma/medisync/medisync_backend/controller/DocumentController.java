package ma.medisync.medisync_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ma.medisync.medisync_backend.service.DocumentStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Documents", description = "Document storage and retrieval API")
@SecurityRequirement(name = "JWT")
public class DocumentController {
    @Autowired
    private DocumentStorageService storageService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Upload a document", description = "Upload a file to storage")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws Exception {
        String fileName = storageService.storeFile(file);
        return new ResponseEntity<>(fileName, HttpStatus.CREATED);
    }

    @GetMapping("/download/{fileName}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Download a document", description = "Download a file by name")
    public ResponseEntity<byte[]> download(@PathVariable String fileName) throws Exception {
        byte[] fileContent = storageService.getFile(fileName);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .body(fileContent);
    }

    @DeleteMapping("/{fileName}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Delete a document", description = "Delete a file from storage")
    public ResponseEntity<Void> delete(@PathVariable String fileName) throws Exception {
        storageService.deleteFile(fileName);
        return ResponseEntity.noContent().build();
    }
}