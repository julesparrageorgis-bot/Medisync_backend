package ma.medisync.medisync_backend.service;

import ma.medisync.medisync_backend.entity.Appointment;
import ma.medisync.medisync_backend.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReminderScheduler {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 * * * *") // every hour
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        sendWindow(now.plusHours(24), true);
        sendWindow(now.plusHours(1), false);
    }

    private void sendWindow(LocalDateTime target, boolean twentyFourHours) {
        List<Appointment> appointments = appointmentRepository.findByAppointmentDateBetween(
                target.minusMinutes(30), target.plusMinutes(30));
        for (Appointment appointment : appointments) {
            if ("CANCELLED".equals(appointment.getStatus()) || "COMPLETED".equals(appointment.getStatus())) {
                continue;
            }
            if (twentyFourHours && Boolean.TRUE.equals(appointment.getReminder24hSent())) {
                continue;
            }
            if (!twentyFourHours && Boolean.TRUE.equals(appointment.getReminder1hSent())) {
                continue;
            }
            String label = twentyFourHours ? "24 hours" : "1 hour";
            emailService.sendBestEffort(appointment.getPatient().getEmail(), "MediSync appointment reminder",
                    "Reminder: your appointment is in " + label + ", at " + appointment.getAppointmentDate());
            if (twentyFourHours) {
                appointment.setReminder24hSent(true);
            } else {
                appointment.setReminder1hSent(true);
            }
            appointmentRepository.save(appointment);
        }
    }
}
