package com.medisync.service;

import com.medisync.entity.Appointment;
import com.medisync.repository.AppointmentRepository;
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
        List<Appointment> appointments = appointmentRepository.findByStartTimeBetween(now.plusHours(23), now.plusHours(25));
        for (Appointment a : appointments) {
            emailService.sendSimpleEmail(a.getPatient().getEmail(),
                    "Rendez-vous demain",
                    "Votre rendez-vous avec Dr. " + a.getDoctor().getLastName() + " est demain à " + a.getStartTime());
        }
        // 1-hour reminders
        appointments = appointmentRepository.findByStartTimeBetween(now.plusMinutes(55), now.plusMinutes(65));
        for (Appointment a : appointments) {
            emailService.sendSimpleEmail(a.getPatient().getEmail(),
                    "Rendez-vous dans 1 heure",
                    "Votre rendez-vous commence dans 1 heure.");
        }
    }
}