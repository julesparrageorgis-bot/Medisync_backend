package ma.medisync.medisync_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.dto.LoginRequest;
import ma.medisync.medisync_backend.dto.LoginResponse;
import ma.medisync.medisync_backend.dto.RegisterRequest;
import ma.medisync.medisync_backend.entity.User;
import ma.medisync.medisync_backend.entity.Patient;
import ma.medisync.medisync_backend.entity.enums.UserRole;
import ma.medisync.medisync_backend.service.UserService;
import ma.medisync.medisync_backend.util.JwtTokenProvider;
import ma.medisync.medisync_backend.util.PasswordUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and authorization API")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with email and password")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        String token = tokenProvider.generateToken(authentication);
        User user = userService.findByEmail(loginRequest.getEmail());
        if (Boolean.TRUE.equals(user.getTwoFactorEnabled())) {
            user.setTwoFactorVerified(false);
            userService.save(user);
        }

        return ResponseEntity.ok(LoginResponse.builder()
            .token(token)
            .userId(user.getId())
            .email(user.getEmail())
            .role(user.getUserRole().toString())
            .twoFactorRequired(user.getUserRole() == UserRole.ADMIN || Boolean.TRUE.equals(user.getTwoFactorEnabled()))
            .build());
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new patient user")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        PasswordUtil.validatePasswordStrength(registerRequest.getPassword());

        Patient user = new Patient();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setUserRole(UserRole.PATIENT);
        user.setIsActive(true);

        userService.save(user);

        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @PostMapping("/password-reset-request")
    @Operation(summary = "Request password reset", description = "Request a password reset token")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            // Return generic message for security
            return ResponseEntity.ok("If email exists, password reset link has been sent");
        }
        // TODO: Generate reset token and send email
        return ResponseEntity.ok("If email exists, password reset link has been sent");
    }

    @PostMapping("/password-reset-confirm")
    @Operation(summary = "Confirm password reset", description = "Confirm password reset with token and new password")
    public ResponseEntity<String> confirmPasswordReset(@RequestParam String token, @RequestParam String newPassword) {
        // TODO: Validate token and update password
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/refresh-token")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Refresh JWT token", description = "Get a new JWT token using current authentication")
    public ResponseEntity<LoginResponse> refreshToken() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            email, null, userService.loadUserByUsername(email).getAuthorities()
        );
        
        String token = tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(LoginResponse.builder()
            .token(token)
            .userId(user.getId())
            .email(user.getEmail())
            .role(user.getUserRole().toString())
            .twoFactorRequired(user.getUserRole() == UserRole.ADMIN || Boolean.TRUE.equals(user.getTwoFactorEnabled()))
            .build());
    }
}
