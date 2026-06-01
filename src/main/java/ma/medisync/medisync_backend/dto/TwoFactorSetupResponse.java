package ma.medisync.medisync_backend.dto;

import lombok.Data;

@Data
public class TwoFactorSetupResponse {
    private String secret;
    private String qrUri;
}