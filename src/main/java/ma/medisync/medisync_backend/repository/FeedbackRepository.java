package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Review, Long> {
    List<Review> findByPatientId(Long patientId);
}