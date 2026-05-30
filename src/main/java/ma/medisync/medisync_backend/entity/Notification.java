package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 100, nullable = false)
    private String type; // APPOINTMENT_REMINDER, PRESCRIPTION_READY, INVOICE_PAYMENT, etc.

    @Column(length = 255, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Column(length = 50)
    private String notificationChannel; // EMAIL, SMS, IN_APP, PUSH

    @Column(nullable = false)
    @Builder.Default
    private Boolean isSent = false;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime sentAt;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime readAt;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(length = 100)
    private String relatedEntityType; // e.g., Appointment, Invoice

    @Column
    private Long relatedEntityId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
