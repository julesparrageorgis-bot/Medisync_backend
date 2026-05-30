package ma.medisync.medisync_backend.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    
    private Long id;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    private Long appointmentId;
    
    @NotNull(message = "Rating is required")
    @Min(1)
    @Max(5)
    private Integer rating;
    
    @NotBlank(message = "Comment is required")
    @Size(min = 10, max = 500)
    private String comment;
    
    private Boolean isAnonymous;
    
    private Boolean isApproved;
    
    private String adminNote;
    
    private Boolean isHelpful;
    
    private Integer helpfulCount;
}
