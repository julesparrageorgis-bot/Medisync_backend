package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String invoiceNumber;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(nullable = false)
    private LocalDate invoiceDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    @Builder.Default
    private Double subtotal = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double taxAmount = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double discountAmount = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double totalAmount = 0.0;

    @Column(length = 50)
    private String status; // DRAFT, PENDING, PAID, OVERDUE, CANCELLED

    @Column(length = 50)
    private String paymentMethod; // CASH, CREDIT_CARD, CHECK, INSURANCE, etc.

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPaid = false;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime paidAt;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
    }
}
