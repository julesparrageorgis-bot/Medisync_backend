package ma.medisync.medisync_backend.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDTO {
    
    private Long id;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    @NotNull(message = "Appointment date is required")
    private LocalDateTime appointmentDate;
    
    @Size(max = 50)
    private String reason;
    
    @Pattern(regexp = "^(SCHEDULED|CONFIRMED|COMPLETED|CANCELLED|NO_SHOW)$", 
             message = "Status must be valid")
    private String status;
    
    @Size(max = 255)
    private String notes;
    
    @Size(max = 20)
    private String consultationType; // GENERAL, FOLLOW_UP, URGENT
    
    private Integer duration; // in minutes
    
    private Boolean reminderSent;
    
    private String consultationRoom;
}
