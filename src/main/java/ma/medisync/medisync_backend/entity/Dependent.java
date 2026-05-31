package com.medisync.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Dependent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_patient_id")
    private Patient parentPatient;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String relationship; // "child", "elderly", etc.
}