package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Invoice invoice;

    private BigDecimal amountPaid;
    private String paymentMethod; // CASH, CARD, TRANSFER
    private LocalDateTime paymentDate;
}