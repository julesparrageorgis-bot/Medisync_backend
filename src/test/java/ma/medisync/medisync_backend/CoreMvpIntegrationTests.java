package ma.medisync.medisync_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.medisync.medisync_backend.entity.Appointment;
import ma.medisync.medisync_backend.entity.Doctor;
import ma.medisync.medisync_backend.entity.Patient;
import ma.medisync.medisync_backend.entity.TimeSlot;
import ma.medisync.medisync_backend.entity.enums.UserRole;
import ma.medisync.medisync_backend.exception.ValidationException;
import ma.medisync.medisync_backend.repository.DoctorRepository;
import ma.medisync.medisync_backend.repository.PatientRepository;
import ma.medisync.medisync_backend.repository.TimeSlotRepository;
import ma.medisync.medisync_backend.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class CoreMvpIntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private TimeSlotRepository timeSlotRepository;
    @Autowired
    private AppointmentService appointmentService;

    @Test
    void registrationLoginAndProtectedAccessWork() throws Exception {
        String email = "mvp-patient@example.com";
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "Password1!",
                                  "firstName": "Mvp",
                                  "lastName": "Patient"
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated());

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"email": "%s", "password": "Password1!"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("PATIENT"))
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("token").asText();
        mockMvc.perform(get("/api/appointments")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void bookingReservesSlotAndRejectsDuplicateBooking() {
        Patient patient = createPatient("booker@example.com");
        Doctor doctor = createDoctor("doctor@example.com");
        LocalDateTime start = LocalDateTime.now().plusDays(2).withNano(0);
        timeSlotRepository.save(TimeSlot.builder()
                .doctor(doctor)
                .startTime(start)
                .endTime(start.plusMinutes(30))
                .build());

        appointmentService.createAppointment(appointment(patient, doctor, start));

        assertThrows(ValidationException.class,
                () -> appointmentService.createAppointment(appointment(patient, doctor, start)));
    }

    private Patient createPatient(String email) {
        Patient patient = new Patient();
        patient.setEmail(email);
        patient.setPassword(passwordEncoder.encode("Password1!"));
        patient.setUserRole(UserRole.PATIENT);
        patient.setIsActive(true);
        return patientRepository.save(patient);
    }

    private Doctor createDoctor(String email) {
        Doctor doctor = new Doctor();
        doctor.setEmail(email);
        doctor.setPassword(passwordEncoder.encode("Password1!"));
        doctor.setUserRole(UserRole.DOCTOR);
        doctor.setIsActive(true);
        doctor.setLicenseNumber("LICENSE-" + System.nanoTime());
        return doctorRepository.save(doctor);
    }

    private Appointment appointment(Patient patient, Doctor doctor, LocalDateTime start) {
        return Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(start)
                .build();
    }
}
