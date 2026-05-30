package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @Column(length = 100)
    private String genericName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String dosage; // e.g., "500mg"

    @Column(length = 50)
    private String form; // TABLET, CAPSULE, LIQUID, INJECTION, etc.

    @Column(length = 50)
    private String manufacturer;

    @Column(length = 50)
    private String batchNumber;

    @Column(nullable = false)
    @Builder.Default
    private Double price = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;

    @Column(length = 255)
    private String sideEffects;

    @Column(length = 255)
    private String contraindications;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
