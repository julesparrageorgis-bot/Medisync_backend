package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "invoice_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(length = 255, nullable = false)
    private String description; // e.g., "Consultation Fee", "Lab Test", etc.

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Column(nullable = false)
    @Builder.Default
    private Double unitPrice = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double taxRate = 0.0; // percentage

    @Column(nullable = false)
    @Builder.Default
    private Double totalPrice = 0.0;

    @Column(length = 50)
    private String category; // CONSULTATION, TESTS, MEDICATIONS, etc.

    @Column(columnDefinition = "TEXT")
    private String notes;
}
