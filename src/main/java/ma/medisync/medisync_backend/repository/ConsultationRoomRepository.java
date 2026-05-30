package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.ConsultationRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConsultationRoomRepository extends JpaRepository<ConsultationRoom, Long> {
    List<ConsultationRoom> findByOfficeId(Long officeId);
    List<ConsultationRoom> findByIsAvailableTrue();
}
