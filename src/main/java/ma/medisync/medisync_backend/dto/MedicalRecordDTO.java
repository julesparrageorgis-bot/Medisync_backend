package ma.medisync.medisync_backend.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordDTO {
    
    private Long id;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    private LocalDateTime recordDate;
    
    @Pattern(regexp = "^(CONSULTATION|LAB_RESULT|IMAGING)$", message = "Record type must be valid")
    private String recordType;
    
    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;
    
    private String symptoms;
    
    private String treatment;
    
    @Size(max = 500)
    private String notes;
    
    private String prescriptionDetails;
    
    private String imageUrl;
    
    private Boolean isConfidential;
}
