package ma.medisync.medisync_backend.controller;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Review;
import ma.medisync.medisync_backend.service.ReviewService;
import ma.medisync.medisync_backend.service.SecurityService;
import ma.medisync.medisync_backend.service.ApiResponseMapper;
import ma.medisync.medisync_backend.dto.ApiResponses.ReviewResponse;
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
    private final ApiResponseMapper mapper;

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("isAuthenticated()")
    public List<ReviewResponse> approved(@PathVariable Long doctorId) {
        return reviewService.getApprovedReviews(doctorId).stream().map(mapper::review).toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ReviewResponse> create(@RequestBody Review review) {
        review.getPatient().setId(securityService.currentUser().getId());
        return ResponseEntity.status(201).body(mapper.review(reviewService.createReview(review)));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReviewResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.review(reviewService.approveReview(id)));
    }
}
