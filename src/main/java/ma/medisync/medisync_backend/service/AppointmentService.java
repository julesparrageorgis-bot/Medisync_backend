package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Appointment;
import ma.medisync.medisync_backend.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public Appointment createAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status);
    }

    public List<Appointment> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByAppointmentDateBetween(start, end);
    }

    public List<Appointment> getPatientAppointmentsByStatus(Long patientId, String status) {
        return appointmentRepository.findByPatientIdAndStatus(patientId, status);
    }

    public List<Appointment> getDoctorAppointmentsByStatus(Long doctorId, String status) {
        return appointmentRepository.findByDoctorIdAndStatus(doctorId, status);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        if (appointment.isPresent()) {
            Appointment a = appointment.get();
            a.setAppointmentDate(appointmentDetails.getAppointmentDate());
            a.setAppointmentTime(appointmentDetails.getAppointmentTime());
            a.setStatus(appointmentDetails.getStatus());
            a.setReason(appointmentDetails.getReason());
            a.setNotes(appointmentDetails.getNotes());
            a.setConsultationFee(appointmentDetails.getConsultationFee());
            return appointmentRepository.save(a);
        }
        return null;
    }

    public boolean deleteAppointment(Long id) {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findAll().stream()
                .filter(appt -> appt.getPatient() != null && appt.getPatient().getId().equals(patientId))
                .toList();
    }

    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findAll().stream()
                .filter(appt -> appt.getDoctor() != null && appt.getDoctor().getId().equals(doctorId))
                .toList();
    }

    public Appointment cancelAppointment(Long id) {
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        if (appointment.isPresent()) {
            Appointment a = appointment.get();
            a.setStatus("CANCELLED");
            return appointmentRepository.save(a);
        }
        return null;
    }

    public Appointment confirmAppointment(Long id) {
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        if (appointment.isPresent()) {
            Appointment a = appointment.get();
            a.setStatus("CONFIRMED");
            return appointmentRepository.save(a);
        }
        return null;
    }
}
