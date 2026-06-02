package ma.medisync.medisync_backend.controller;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.dto.AvailabilityRequest;
import ma.medisync.medisync_backend.dto.AvailabilityResponse;
import ma.medisync.medisync_backend.entity.TimeSlot;
import ma.medisync.medisync_backend.service.TimeSlotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-slots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @GetMapping("/doctor/{doctorId}/available")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'SECRETARY', 'ADMIN')")
    public List<AvailabilityResponse> getDoctorAvailableSlots(@PathVariable Long doctorId) {
        return timeSlotService.getDoctorAvailableSlots(doctorId).stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<AvailabilityResponse> createTimeSlot(@RequestBody AvailabilityRequest request) {
        TimeSlot slot = timeSlotService.createTimeSlot(
                request.getDoctorId(), request.getStartTime(), request.getEndTime());
        return new ResponseEntity<>(toResponse(slot), HttpStatus.CREATED);
    }

    private AvailabilityResponse toResponse(TimeSlot slot) {
        AvailabilityResponse response = new AvailabilityResponse();
        response.setId(slot.getId());
        response.setDoctorId(slot.getDoctor().getId());
        response.setStartTime(slot.getStartTime());
        response.setEndTime(slot.getEndTime());
        response.setBooked(!Boolean.TRUE.equals(slot.getIsAvailable()));
        return response;
    }
}
