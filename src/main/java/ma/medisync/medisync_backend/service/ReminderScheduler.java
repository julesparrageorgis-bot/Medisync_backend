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
        // TODO: Implement appointment reminders
        // Find appointments scheduled for 24 hours from now
        // Find appointments scheduled for 1 hour from now
        // Send email reminders
    }
}