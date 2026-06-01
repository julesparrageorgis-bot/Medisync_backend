package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByStatus(String status);
    List<Appointment> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);
    List<Appointment> findByPatientIdAndStatus(Long patientId, String status);
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, String status);
    long countByStatus(String status);
}
