package ma.medisync.medisync_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type; // consultation, surgery, etc.
    private boolean available;
}