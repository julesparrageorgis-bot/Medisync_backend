package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class IssueReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private User reporter;

    private String title;
    private String description;
    private String status; // OPEN, IN_PROGRESS, RESOLVED
    private LocalDateTime createdAt;
}