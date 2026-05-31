package com.medisync.dto;

import lombok.Data;

@Data
public class ClinicSettingsRequest {
    private String name;
    private String address;
    private String phone;
    private String email;
    private String openingHours;
    private String specialties;
}