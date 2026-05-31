package com.medisync.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DashboardResponse {
    private double occupancyRate;
    private double noShowRate;
    private BigDecimal totalRevenue;
}