package com.medisync.repository;

import com.medisync.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByDoctorIdAndStartTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);
    List<Availability> findByDoctorIdAndIsBookedFalse(Long doctorId);
}