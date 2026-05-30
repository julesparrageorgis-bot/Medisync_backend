package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.Secretary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SecretaryRepository extends JpaRepository<Secretary, Long> {
    Optional<Secretary> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
