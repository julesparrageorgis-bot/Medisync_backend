package com.medisync.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MedicalReportTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String content;
    private String specialty; // optional
}