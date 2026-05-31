package com.medisync.repository;

import com.medisync.entity.PerformedAct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PerformedActRepository extends JpaRepository<PerformedAct, Long> {
    List<PerformedAct> findByAppointmentId(Long appointmentId);
}