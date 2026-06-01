package ma.medisync.medisync_backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Long invoiceId;
    private BigDecimal amountPaid;
    private String paymentMethod; // CASH, CARD, TRANSFER
}