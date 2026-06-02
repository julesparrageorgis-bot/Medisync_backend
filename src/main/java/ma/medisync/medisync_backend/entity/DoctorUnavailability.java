package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class DoctorUnavailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Doctor doctor;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reason;
    private boolean leaveDay;
}
