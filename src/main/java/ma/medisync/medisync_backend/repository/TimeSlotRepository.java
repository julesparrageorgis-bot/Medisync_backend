package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByDoctorId(Long doctorId);
    List<TimeSlot> findByDoctorIdAndIsAvailableTrue(Long doctorId);
    List<TimeSlot> findByStartTimeGreaterThanEqual(LocalDateTime startTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TimeSlot> findByDoctorIdAndStartTime(Long doctorId, LocalDateTime startTime);
}
