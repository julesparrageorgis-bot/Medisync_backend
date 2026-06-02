package ma.medisync.medisync_backend.controller;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Notification;
import ma.medisync.medisync_backend.service.NotificationService;
import ma.medisync.medisync_backend.service.SecurityService;
import ma.medisync.medisync_backend.service.ApiResponseMapper;
import ma.medisync.medisync_backend.dto.ApiResponses.NotificationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityService securityService;
    private final ApiResponseMapper mapper;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<NotificationResponse> mine() {
        return notificationService.getUserNotifications(securityService.currentUser().getId()).stream()
                .map(mapper::notification).toList();
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationResponse> read(@PathVariable Long id) {
        Notification notification = notificationService.getNotificationById(id).orElseThrow();
        if (!notification.getUser().getId().equals(securityService.currentUser().getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Notification ownership required");
        }
        return ResponseEntity.ok(mapper.notification(notificationService.markAsRead(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationResponse> create(@RequestBody Notification notification) {
        return ResponseEntity.status(201).body(mapper.notification(notificationService.createNotification(notification)));
    }
}
