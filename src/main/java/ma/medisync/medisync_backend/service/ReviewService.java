package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Review;
import ma.medisync.medisync_backend.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Review createReview(Review review) {
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    public List<Review> getDoctorReviews(Long doctorId) {
        return reviewRepository.findByDoctorId(doctorId);
    }

    public List<Review> getPatientReviews(Long patientId) {
        return reviewRepository.findByPatientId(patientId);
    }

    public List<Review> getApprovedReviews(Long doctorId) {
        return reviewRepository.findByDoctorIdAndIsApprovedTrue(doctorId);
    }

    public List<Review> getPendingReviews() {
        return reviewRepository.findAll().stream()
                .filter(r -> !r.getIsApproved())
                .toList();
    }

    public Double getAverageRating(Long doctorId) {
        List<Review> reviews = getApprovedReviews(doctorId);
        if (reviews.isEmpty()) return 0.0;
        return reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public List<Review> getReviewsByRating(Long doctorId, Integer rating) {
        return reviewRepository.findAll().stream()
                .filter(r -> r.getDoctor().getId().equals(doctorId) && r.getRating().equals(rating))
                .toList();
    }

    public List<Review> getReviewsByDateRange(LocalDateTime start, LocalDateTime end) {
        return reviewRepository.findAll().stream()
                .filter(r -> !r.getCreatedAt().isBefore(start) && !r.getCreatedAt().isAfter(end))
                .toList();
    }

    public Review approveReview(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isPresent()) {
            Review r = review.get();
            r.setIsApproved(true);
            return reviewRepository.save(r);
        }
        return null;
    }

    public Review rejectReview(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isPresent()) {
            Review r = review.get();
            r.setIsApproved(false);
            return reviewRepository.save(r);
        }
        return null;
    }

    public Review updateReview(Long id, Review reviewDetails) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isPresent()) {
            Review r = review.get();
            r.setRating(reviewDetails.getRating());
            r.setComment(reviewDetails.getComment());
            return reviewRepository.save(r);
        }
        return null;
    }

    public boolean deleteReview(Long id) {
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
