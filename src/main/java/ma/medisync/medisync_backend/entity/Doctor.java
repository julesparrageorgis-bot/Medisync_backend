package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 100, nullable = false)
    private String specialization; // Cardiology, Dermatology, etc.

    @Column(length = 20, nullable = false)
    private String licenseNumber;

    @Column(nullable = false)
    @Builder.Default
    private Double consultationFee = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Integer yearsOfExperience = 0;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(length = 20)
    private String officeLocation;
}
