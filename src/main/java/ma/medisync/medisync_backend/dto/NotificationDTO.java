package ma.medisync.medisync_backend.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    
    private Long id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100)
    private String title;
    
    @NotBlank(message = "Message is required")
    @Size(min = 5, max = 500)
    private String message;
    
    @Pattern(regexp = "^(APPOINTMENT_REMINDER|PRESCRIPTION_READY|INVOICE_PAYMENT|GENERAL|URGENT)$", 
             message = "Type must be valid")
    private String type;
    
    @Pattern(regexp = "^(EMAIL|SMS|IN_APP|PUSH)$", message = "Channel must be valid")
    private String channel;
    
    private Boolean isRead;
    
    private Boolean isSent;
    
    private String relatedEntityType;
    
    private Long relatedEntityId;
    
    private Integer priority; // 1-5, where 5 is highest
}
