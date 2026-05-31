package com.medisync.dto;

import lombok.Data;

@Data
public class TwoFactorVerifyRequest {
    private String code;
}