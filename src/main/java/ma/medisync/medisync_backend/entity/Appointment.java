package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "dependent_id")
    private Dependent dependent;

    @ManyToOne
    @JoinColumn(name = "consultation_room_id")
    private ConsultationRoom consultationRoom;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime appointmentDate;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime appointmentTime;

    @Column(length = 50, nullable = false)
    private String status; // SCHEDULED, CONFIRMED, COMPLETED, CANCELLED, NO_SHOW

    @Column(columnDefinition = "TEXT")
    private String reason; // Why the appointment was booked

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    @Builder.Default
    private Double consultationFee = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Integer durationMinutes = 30;

    @Column(nullable = false)
    @Builder.Default
    private Boolean emergency = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean reminder24hSent = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean reminder1hSent = false;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = "SCHEDULED";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
