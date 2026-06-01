package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
public class Tariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Doctor doctor;

    private String actType; // consultation, follow-up, emergency
    private String sector; // 1, 2, 3
    private BigDecimal price;
}