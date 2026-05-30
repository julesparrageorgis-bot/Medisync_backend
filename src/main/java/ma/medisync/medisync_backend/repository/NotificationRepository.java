package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    List<Notification> findByType(String type);
    List<Notification> findByNotificationChannel(String channel);
    List<Notification> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
