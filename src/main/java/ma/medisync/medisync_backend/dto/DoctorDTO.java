package ma.medisync.medisync_backend.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorDTO {
    
    private Long id;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    private String lastName;
    
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9+\\-\\s()]{10,}$", message = "Phone number is invalid")
    private String phoneNumber;
    
    @NotBlank(message = "Specialization is required")
    private String specialization;
    
    @NotBlank(message = "License number is required")
    private String licenseNumber;
    
    private String qualifications;
    
    private Integer yearsOfExperience;
    
    @DecimalMin("0.0")
    private Double consultationFee;
    
    private String languages;
    
    private Boolean isAvailable;
    
    private String bio;
    
    private String workingHours;
}
