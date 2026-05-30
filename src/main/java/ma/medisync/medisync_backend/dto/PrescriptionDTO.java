package ma.medisync.medisync_backend.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionDTO {
    
    private Long id;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    @NotBlank(message = "Prescription number is required")
    private String prescriptionNumber;
    
    @NotNull(message = "Issue date is required")
    private LocalDate issueDate;
    
    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;
    
    @NotBlank(message = "Medication name is required")
    private String medicationName;
    
    @NotBlank(message = "Dosage is required")
    private String dosage;
    
    @NotNull(message = "Quantity is required")
    @Min(1)
    private Integer quantity;
    
    @NotBlank(message = "Instructions are required")
    private String instructions;
    
    private String notes;
    
    @Pattern(regexp = "^(ACTIVE|EXPIRED|FILLED|PENDING)$", message = "Status must be valid")
    private String status;
    
    private Integer refillsRemaining;
    
    private Boolean isPrintable;
}
