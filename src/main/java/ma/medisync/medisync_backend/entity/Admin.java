package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 100, nullable = false)
    private String adminLevel; // SUPER_ADMIN, CLINIC_ADMIN, etc.

    @Column(nullable = false)
    @Builder.Default
    private Boolean can2FA = true;

    @Column(length = 255)
    private String twoFactorSecret; // For TOTP

    @Column(nullable = false)
    @Builder.Default
    private Boolean twoFactorEnabled = false;

    @Column(columnDefinition = "TEXT")
    private String permissions; // JSON or comma-separated

    @Column(nullable = false)
    @Builder.Default
    private Boolean canAccessReports = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean canManageUsers = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean canManageClinic = true;
}
