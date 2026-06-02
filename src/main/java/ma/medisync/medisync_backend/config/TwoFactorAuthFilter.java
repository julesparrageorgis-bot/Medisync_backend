package ma.medisync.medisync_backend.config;

import ma.medisync.medisync_backend.entity.User;
import ma.medisync.medisync_backend.entity.enums.UserRole;
import ma.medisync.medisync_backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class TwoFactorAuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public TwoFactorAuthFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !request.getRequestURI().startsWith("/api/auth/2fa")) {
            String email = auth.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            boolean mandatoryAdminSetup = user != null && user.getUserRole() == UserRole.ADMIN &&
                    !Boolean.TRUE.equals(user.getTwoFactorEnabled());
            boolean verificationRequired = user != null && Boolean.TRUE.equals(user.getTwoFactorEnabled()) &&
                    !Boolean.TRUE.equals(user.getTwoFactorVerified());
            if (mandatoryAdminSetup || verificationRequired) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Admin 2FA setup or verification required");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
