package ma.medisync.medisync_backend.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {
    
    private Long id;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotBlank(message = "Invoice number is required")
    private String invoiceNumber;
    
    @NotNull(message = "Invoice date is required")
    private LocalDate invoiceDate;
    
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
    
    @NotNull(message = "Total amount is required")
    @DecimalMin("0.0")
    private BigDecimal totalAmount;
    
    @DecimalMin("0.0")
    private BigDecimal subtotal;
    
    @DecimalMin("0.0")
    private BigDecimal taxAmount;
    
    @DecimalMin("0.0")
    private BigDecimal discountAmount;
    
    @Pattern(regexp = "^(DRAFT|PENDING|PAID|OVERDUE|CANCELLED)$", message = "Status must be valid")
    private String status;
    
    private String paymentMethod;
    
    private LocalDate paymentDate;
    
    @Size(max = 500)
    private String description;
    
    private String notes;
    
    private Boolean isPrintable;
}
