package com.medisync.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ClinicSettings {
    @Id
    private Long id = 1L; // singleton

    private String name;
    private String address;
    private String phone;
    private String email;
    private String openingHours;
    private String specialties; // comma separated
}