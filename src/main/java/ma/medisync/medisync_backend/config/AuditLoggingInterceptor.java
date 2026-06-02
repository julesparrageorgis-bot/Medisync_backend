package ma.medisync.medisync_backend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.AuditLog;
import ma.medisync.medisync_backend.entity.User;
import ma.medisync.medisync_backend.repository.AuditLogRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuditLoggingInterceptor implements HandlerInterceptor {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        if (!isSensitiveWrite(request.getMethod())) {
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return;
        }
        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action(request.getMethod())
                .entityType(request.getRequestURI())
                .entityId(0L)
                .description("HTTP " + request.getMethod() + " " + request.getRequestURI())
                .status(response.getStatus() < 400 && ex == null ? "SUCCESS" : "FAILED")
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .isSecurityEvent(request.getRequestURI().startsWith("/api/auth"))
                .build());
    }

    private boolean isSensitiveWrite(String method) {
        return "POST".equals(method) || "PUT".equals(method) ||
                "PATCH".equals(method) || "DELETE".equals(method);
    }
}
