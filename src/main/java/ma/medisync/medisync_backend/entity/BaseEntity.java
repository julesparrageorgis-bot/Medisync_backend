package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    protected LocalDateTime updatedAt;
}
