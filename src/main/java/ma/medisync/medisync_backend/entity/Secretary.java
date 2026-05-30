package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "secretaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Secretary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 100)
    private String officeAssigned;

    @Column(length = 50)
    private String department;

    @Column(nullable = false)
    @Builder.Default
    private Boolean canManageAppointments = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean canManagePatients = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean canGenerateInvoices = false;

    @Column(length = 50)
    private String workSchedule; // e.g., "Monday-Friday 9AM-5PM"
}
