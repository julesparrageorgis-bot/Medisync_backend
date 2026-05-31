package com.medisync.dto;

import lombok.Data;
import java.util.List;

@Data
public class InvoiceRequest {
    private Long appointmentId;
    private List<Long> performedActIds;
}