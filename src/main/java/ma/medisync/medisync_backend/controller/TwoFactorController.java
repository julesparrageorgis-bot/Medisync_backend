package com.medisync.controller;

import com.medisync.config.TwoFactorAuthService;
import com.medisync.dto.TwoFactorSetupResponse;
import com.medisync.dto.TwoFactorVerifyRequest;
import com.medisync.entity.User;
import com.medisync.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/2fa")
public class TwoFactorController {
    @Autowired
    private TwoFactorAuthService twoFactorService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/setup")
    public ResponseEntity<TwoFactorSetupResponse> setup() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        String secret = twoFactorService.generateSecret();
        user.setTwoFactorSecret(secret);
        userRepository.save(user);
        TwoFactorSetupResponse response = new TwoFactorSetupResponse();
        response.setSecret(secret);
        response.setQrUri(twoFactorService.getQrUri(secret, email));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody TwoFactorVerifyRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        if (twoFactorService.verifyCode(user.getTwoFactorSecret(), request.getCode())) {
            user.setTwoFactorEnabled(true);
            user.setTwoFactorVerified(true);
            userRepository.save(user);
            return ResponseEntity.ok("2FA enabled");
        }
        return ResponseEntity.badRequest().body("Invalid code");
    }
}