package com.medisync.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
public class PerformedAct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Appointment appointment;

    private String actCode;
    private String description;
    private BigDecimal amount;
}