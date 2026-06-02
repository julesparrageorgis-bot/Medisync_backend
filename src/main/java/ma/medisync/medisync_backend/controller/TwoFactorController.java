package ma.medisync.medisync_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ma.medisync.medisync_backend.config.TwoFactorAuthService;
import ma.medisync.medisync_backend.dto.TwoFactorSetupResponse;
import ma.medisync.medisync_backend.dto.TwoFactorVerifyRequest;
import ma.medisync.medisync_backend.entity.User;
import ma.medisync.medisync_backend.entity.enums.UserRole;
import ma.medisync.medisync_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/2fa")
@Tag(name = "Two-Factor Authentication", description = "2FA setup and verification API")
@SecurityRequirement(name = "JWT")
public class TwoFactorController {
    @Autowired
    private TwoFactorAuthService twoFactorService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/setup")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Setup 2FA", description = "Generate secret and QR code for 2FA setup")
    public ResponseEntity<TwoFactorSetupResponse> setup() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> 
            new IllegalArgumentException("User not found"));
        String secret = twoFactorService.generateSecret();
        user.setTwoFactorSecret(secret);
        userRepository.save(user);
        TwoFactorSetupResponse response = new TwoFactorSetupResponse();
        response.setSecret(secret);
        response.setQrUri(twoFactorService.getQrUri(secret, email));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/verify")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Verify 2FA code", description = "Verify 2FA code and enable 2FA")
    public ResponseEntity<String> verify(@RequestBody TwoFactorVerifyRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> 
            new IllegalArgumentException("User not found"));
        if (twoFactorService.verifyCode(user.getTwoFactorSecret(), request.getCode())) {
            user.setTwoFactorEnabled(true);
            user.setTwoFactorVerified(true);
            userRepository.save(user);
            return ResponseEntity.ok("2FA enabled successfully");
        }
        return ResponseEntity.badRequest().body("Invalid verification code");
    }

    @PostMapping("/disable")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    @Operation(summary = "Disable 2FA", description = "Disable 2FA for current user")
    public ResponseEntity<String> disable() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> 
            new IllegalArgumentException("User not found"));
        if (user.getUserRole() == UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("2FA is mandatory for admin accounts");
        }
        user.setTwoFactorEnabled(false);
        user.setTwoFactorVerified(false);
        user.setTwoFactorSecret(null);
        userRepository.save(user);
        return ResponseEntity.ok("2FA disabled successfully");
    }
}
