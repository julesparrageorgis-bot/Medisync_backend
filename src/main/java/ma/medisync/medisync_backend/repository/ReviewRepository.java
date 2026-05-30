package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPatientId(Long patientId);
    List<Review> findByDoctorId(Long doctorId);
    List<Review> findByDoctorIdAndIsApprovedTrue(Long doctorId);
    List<Review> findByRatingGreaterThanEqual(Integer rating);
}
