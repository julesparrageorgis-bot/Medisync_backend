package ma.medisync.medisync_backend.dto;

import lombok.Data;

@Data
public class TwoFactorVerifyRequest {
    private String code;
}