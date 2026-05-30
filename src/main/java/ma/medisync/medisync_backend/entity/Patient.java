package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 20)
    private String bloodType; // O+, O-, A+, A-, B+, B-, AB+, AB-

    @Column(length = 100)
    private String allergies;

    @Column(columnDefinition = "TEXT")
    private String medicalHistory;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isInsured = false;

    @Column(length = 50)
    private String insuranceCompany;

    @Column(length = 50)
    private String insurancePolicyNumber;
}
