package ma.medisync.medisync_backend.controller;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Notification;
import ma.medisync.medisync_backend.service.NotificationService;
import ma.medisync.medisync_backend.service.SecurityService;
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

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Notification> mine() {
        return notificationService.getUserNotifications(securityService.currentUser().getId());
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Notification> read(@PathVariable Long id) {
        Notification notification = notificationService.getNotificationById(id).orElseThrow();
        if (!notification.getUser().getId().equals(securityService.currentUser().getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Notification ownership required");
        }
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Notification> create(@RequestBody Notification notification) {
        return ResponseEntity.status(201).body(notificationService.createNotification(notification));
    }
}
