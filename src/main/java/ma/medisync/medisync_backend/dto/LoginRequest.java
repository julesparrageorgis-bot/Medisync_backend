package ma.medisync.medisync_backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class LoginRequest {
    private String email;
    private String password;
}
