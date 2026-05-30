package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "time_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime startTime;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(length = 50)
    private String dayOfWeek; // MONDAY, TUESDAY, etc.

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRecurring = false;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
