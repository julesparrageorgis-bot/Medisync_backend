package ma.medisync.medisync_backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class LoginResponse {
    private String token;
    private Long userId;
    private String email;
    private String role;
    private Boolean twoFactorRequired;
}
