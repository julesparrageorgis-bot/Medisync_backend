package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {
    List<Office> findByIsActiveTrue();
    Office findByName(String name);
}
