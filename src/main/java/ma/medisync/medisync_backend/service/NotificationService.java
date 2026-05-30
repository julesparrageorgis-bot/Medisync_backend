package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Notification;
import ma.medisync.medisync_backend.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUserUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId);
    }

    public List<Notification> getNotificationsByType(String type) {
        return notificationRepository.findByType(type);
    }

    public List<Notification> getNotificationsByChannel(String channel) {
        return notificationRepository.findByNotificationChannel(channel);
    }

    public List<Notification> getNotificationsByDateRange(LocalDateTime start, LocalDateTime end) {
        return notificationRepository.findByCreatedAtBetween(start, end);
    }

    public Notification markAsRead(Long id) {
        Optional<Notification> notification = notificationRepository.findById(id);
        if (notification.isPresent()) {
            Notification n = notification.get();
            n.setIsRead(true);
            n.setReadAt(LocalDateTime.now());
            return notificationRepository.save(n);
        }
        return null;
    }

    public Notification markAsSent(Long id) {
        Optional<Notification> notification = notificationRepository.findById(id);
        if (notification.isPresent()) {
            Notification n = notification.get();
            n.setIsSent(true);
            n.setSentAt(LocalDateTime.now());
            return notificationRepository.save(n);
        }
        return null;
    }

    public Notification updateNotification(Long id, Notification notificationDetails) {
        Optional<Notification> notification = notificationRepository.findById(id);
        if (notification.isPresent()) {
            Notification n = notification.get();
            n.setTitle(notificationDetails.getTitle());
            n.setMessage(notificationDetails.getMessage());
            n.setType(notificationDetails.getType());
            return notificationRepository.save(n);
        }
        return null;
    }

    public boolean deleteNotification(Long id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
