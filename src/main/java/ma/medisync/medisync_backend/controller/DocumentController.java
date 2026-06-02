package ma.medisync.medisync_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ma.medisync.medisync_backend.service.DocumentStorageService;
import ma.medisync.medisync_backend.entity.MedicalDocument;
import ma.medisync.medisync_backend.entity.Patient;
import ma.medisync.medisync_backend.service.MedicalDocumentService;
import ma.medisync.medisync_backend.service.SecurityService;
import ma.medisync.medisync_backend.service.ApiResponseMapper;
import ma.medisync.medisync_backend.dto.ApiResponses.MedicalDocumentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Documents", description = "Document storage and retrieval API")
@SecurityRequirement(name = "JWT")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentStorageService storageService;
    private final MedicalDocumentService documentService;
    private final SecurityService securityService;
    private final ApiResponseMapper mapper;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Upload a document", description = "Upload a file to storage")
    public ResponseEntity<MedicalDocumentResponse> upload(@RequestParam("patientId") Long patientId,
                                                   @RequestParam("documentType") String documentType,
                                                   @RequestParam(value = "description", required = false) String description,
                                                   @RequestParam("file") MultipartFile file) throws Exception {
        securityService.assertCanAccessMedicalData(patientId);
        String fileName = storageService.storeFile(file);
        MedicalDocument document = MedicalDocument.builder()
                .patient(patient(patientId))
                .fileName(file.getOriginalFilename())
                .filePath(fileName)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .documentType(documentType)
                .description(description)
                .uploadedBy(securityService.currentUser())
                .build();
        return new ResponseEntity<>(mapper.document(documentService.uploadDocument(document)), HttpStatus.CREATED);
    }

    @GetMapping("/download/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Download a document", description = "Download a file by name")
    public ResponseEntity<byte[]> download(@PathVariable Long id) throws Exception {
        MedicalDocument document = documentService.getDocumentById(id)
                .orElseThrow(() -> ma.medisync.medisync_backend.exception.ResourceNotFoundException.documentNotFound(id));
        securityService.assertCanAccessMedicalData(document.getPatient().getId());
        byte[] fileContent = storageService.getFile(document.getFilePath());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + document.getFileName() + "\"")
                .body(fileContent);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<java.util.List<MedicalDocumentResponse>> patientDocuments(@PathVariable Long patientId) {
        securityService.assertCanAccessMedicalData(patientId);
        return ResponseEntity.ok(documentService.getPatientDocuments(patientId).stream().map(mapper::document).toList());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Delete a document", description = "Delete a file from storage")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        MedicalDocument document = documentService.getDocumentById(id)
                .orElseThrow(() -> ma.medisync.medisync_backend.exception.ResourceNotFoundException.documentNotFound(id));
        securityService.assertCanAccessMedicalData(document.getPatient().getId());
        storageService.deleteFile(document.getFilePath());
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    private Patient patient(Long id) {
        Patient patient = new Patient();
        patient.setId(id);
        return patient;
    }
}
