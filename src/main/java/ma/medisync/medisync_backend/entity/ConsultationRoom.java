package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "consultation_rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @Column(length = 50, nullable = false)
    private String roomNumber;

    @Column(length = 50)
    private String roomType; // General, Pediatric, Surgery, etc.

    @Column(nullable = false)
    @Builder.Default
    private Integer capacity = 1;

    @Column(length = 255)
    private String equipment; // Comma-separated list

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
