package ma.medisync.medisync_backend.controller;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Review;
import ma.medisync.medisync_backend.service.ReviewService;
import ma.medisync.medisync_backend.service.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final SecurityService securityService;

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("isAuthenticated()")
    public List<Review> approved(@PathVariable Long doctorId) {
        return reviewService.getApprovedReviews(doctorId);
    }

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Review> create(@RequestBody Review review) {
        review.getPatient().setId(securityService.currentUser().getId());
        return ResponseEntity.status(201).body(reviewService.createReview(review));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Review> approve(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.approveReview(id));
    }
}
