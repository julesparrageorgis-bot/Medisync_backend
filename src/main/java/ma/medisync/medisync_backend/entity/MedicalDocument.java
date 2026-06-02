package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MedicalDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnoreProperties({"password", "socialSecurityNumber", "allergies", "emergencyContact", "emergencyPhone"})
    private Patient patient;

    @Column(length = 100, nullable = false)
    private String fileName;

    @Column(length = 255)
    private String filePath;

    @Column(length = 50)
    private String documentType; // PRESCRIPTION, LAB_REPORT, IMAGING, INVOICE, etc.

    @Column(length = 50)
    private String fileType; // PDF, IMAGE, etc.

    @Column(nullable = false)
    @Builder.Default
    private Long fileSize = 0L; // in bytes

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_id")
    @JsonIgnoreProperties({"password", "twoFactorSecret"})
    private User uploadedBy;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime uploadDate;

    @PrePersist
    protected void onCreate() {
        uploadDate = LocalDateTime.now();
    }
}
