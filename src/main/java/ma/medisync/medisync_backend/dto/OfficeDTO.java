package ma.medisync.medisync_backend.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfficeDTO {
    
    private Long id;
    
    @NotBlank(message = "Office name is required")
    @Size(min = 3, max = 100)
    private String name;
    
    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 100)
    private String address;
    
    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50)
    private String city;
    
    @NotBlank(message = "Zip code is required")
    @Size(min = 2, max = 20)
    private String zipCode;
    
    @Size(max = 50)
    private String country;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9+\\-\\s()]{10,}$", message = "Phone number is invalid")
    private String phone;
    
    @Email(message = "Email should be valid")
    private String email;
    
    private String description;
    
    private String workingHours;
    
    private Boolean isActive;
    
    private Double latitude;
    
    private Double longitude;
}
