package com.medisync.dto;

import lombok.Data;

@Data
public class TwoFactorSetupResponse {
    private String secret;
    private String qrUri;
}