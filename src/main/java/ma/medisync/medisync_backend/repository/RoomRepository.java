package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}