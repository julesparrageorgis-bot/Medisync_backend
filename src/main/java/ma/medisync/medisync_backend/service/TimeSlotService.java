package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.TimeSlot;
import ma.medisync.medisync_backend.entity.Doctor;
import ma.medisync.medisync_backend.exception.ResourceNotFoundException;
import ma.medisync.medisync_backend.exception.ValidationException;
import ma.medisync.medisync_backend.repository.DoctorRepository;
import ma.medisync.medisync_backend.repository.TimeSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final DoctorRepository doctorRepository;

    public TimeSlot createTimeSlot(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null || !startTime.isBefore(endTime)) {
            throw ValidationException.invalidDateRange();
        }
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> ResourceNotFoundException.doctorNotFound(doctorId));
        return createTimeSlot(TimeSlot.builder()
                .doctor(doctor)
                .startTime(startTime)
                .endTime(endTime)
                .dayOfWeek(startTime.getDayOfWeek().name())
                .build());
    }

    public TimeSlot createTimeSlot(TimeSlot timeSlot) {
        return timeSlotRepository.save(timeSlot);
    }

    public Optional<TimeSlot> getTimeSlotById(Long id) {
        return timeSlotRepository.findById(id);
    }

    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotRepository.findAll();
    }

    public List<TimeSlot> getDoctorAvailableSlots(Long doctorId) {
        return timeSlotRepository.findByDoctorIdAndIsAvailableTrue(doctorId);
    }

    public List<TimeSlot> getSlotsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return timeSlotRepository.findAll().stream()
                .filter(slot -> !slot.getStartTime().isBefore(startTime) && !slot.getEndTime().isAfter(endTime))
                .toList();
    }

    public List<TimeSlot> getAvailableSlots() {
        return timeSlotRepository.findAll().stream()
                .filter(slot -> slot.getIsAvailable() != null && slot.getIsAvailable())
                .toList();
    }

    public TimeSlot bookTimeSlot(Long id) {
        Optional<TimeSlot> timeSlot = timeSlotRepository.findById(id);
        if (timeSlot.isPresent()) {
            TimeSlot slot = timeSlot.get();
            slot.setIsAvailable(false);
            return timeSlotRepository.save(slot);
        }
        return null;
    }

    public TimeSlot releaseTimeSlot(Long id) {
        Optional<TimeSlot> timeSlot = timeSlotRepository.findById(id);
        if (timeSlot.isPresent()) {
            TimeSlot slot = timeSlot.get();
            slot.setIsAvailable(true);
            return timeSlotRepository.save(slot);
        }
        return null;
    }

    public TimeSlot updateTimeSlot(Long id, TimeSlot slotDetails) {
        Optional<TimeSlot> timeSlot = timeSlotRepository.findById(id);
        if (timeSlot.isPresent()) {
            TimeSlot slot = timeSlot.get();
            slot.setStartTime(slotDetails.getStartTime());
            slot.setEndTime(slotDetails.getEndTime());
            slot.setIsAvailable(slotDetails.getIsAvailable());
            slot.setDayOfWeek(slotDetails.getDayOfWeek());
            slot.setIsRecurring(slotDetails.getIsRecurring());
            return timeSlotRepository.save(slot);
        }
        return null;
    }

    public boolean deleteTimeSlot(Long id) {
        if (timeSlotRepository.existsById(id)) {
            timeSlotRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<TimeSlot> getRecurringSlots() {
        return timeSlotRepository.findAll().stream()
                .filter(TimeSlot::getIsRecurring)
                .toList();
    }

    public List<TimeSlot> getSlotsByDayOfWeek(String dayOfWeek) {
        return timeSlotRepository.findAll().stream()
                .filter(slot -> slot.getDayOfWeek().equals(dayOfWeek))
                .toList();
    }
}
