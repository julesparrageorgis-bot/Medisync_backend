package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.ConsultationRoom;
import ma.medisync.medisync_backend.repository.ConsultationRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsultationRoomService {

    private final ConsultationRoomRepository consultationRoomRepository;

    public ConsultationRoom createRoom(ConsultationRoom room) {
        return consultationRoomRepository.save(room);
    }

    public Optional<ConsultationRoom> getRoomById(Long id) {
        return consultationRoomRepository.findById(id);
    }

    public List<ConsultationRoom> getAllRooms() {
        return consultationRoomRepository.findAll();
    }

    public List<ConsultationRoom> getAvailableRooms() {
        return consultationRoomRepository.findByIsAvailableTrue();
    }

    public List<ConsultationRoom> getRoomsByOffice(Long officeId) {
        return consultationRoomRepository.findByOfficeId(officeId);
    }

    public ConsultationRoom updateRoom(Long id, ConsultationRoom roomDetails) {
        Optional<ConsultationRoom> room = consultationRoomRepository.findById(id);
        if (room.isPresent()) {
            ConsultationRoom r = room.get();
            r.setRoomNumber(roomDetails.getRoomNumber());
            r.setRoomType(roomDetails.getRoomType());
            r.setCapacity(roomDetails.getCapacity());
            r.setIsAvailable(roomDetails.getIsAvailable());
            r.setEquipment(roomDetails.getEquipment());
            r.setNotes(roomDetails.getNotes());
            return consultationRoomRepository.save(r);
        }
        return null;
    }

    public boolean makeRoomAvailable(Long id) {
        Optional<ConsultationRoom> room = consultationRoomRepository.findById(id);
        if (room.isPresent()) {
            ConsultationRoom r = room.get();
            r.setIsAvailable(true);
            consultationRoomRepository.save(r);
            return true;
        }
        return false;
    }

    public boolean makeRoomUnavailable(Long id) {
        Optional<ConsultationRoom> room = consultationRoomRepository.findById(id);
        if (room.isPresent()) {
            ConsultationRoom r = room.get();
            r.setIsAvailable(false);
            consultationRoomRepository.save(r);
            return true;
        }
        return false;
    }

    public boolean deleteRoom(Long id) {
        if (consultationRoomRepository.existsById(id)) {
            consultationRoomRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
