package ma.medisync.medisync_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.medisync.medisync_backend.entity.Appointment;
import ma.medisync.medisync_backend.entity.Doctor;
import ma.medisync.medisync_backend.entity.Patient;
import ma.medisync.medisync_backend.entity.TimeSlot;
import ma.medisync.medisync_backend.entity.User;
import ma.medisync.medisync_backend.entity.enums.UserRole;
import ma.medisync.medisync_backend.exception.ValidationException;
import ma.medisync.medisync_backend.repository.DoctorRepository;
import ma.medisync.medisync_backend.repository.PatientRepository;
import ma.medisync.medisync_backend.repository.TimeSlotRepository;
import ma.medisync.medisync_backend.repository.UserRepository;
import ma.medisync.medisync_backend.service.AppointmentService;
import ma.medisync.medisync_backend.service.TimeSlotService;
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
    @Autowired
    private TimeSlotService timeSlotService;
    @Autowired
    private UserRepository userRepository;

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

    @Test
    void timeSlotsRejectUnsupportedDurationsAndOverlaps() {
        Doctor doctor = createDoctor("slot-doctor@example.com");
        LocalDateTime start = LocalDateTime.now().plusDays(3).withNano(0);

        assertThrows(ValidationException.class,
                () -> timeSlotService.createTimeSlot(doctor.getId(), start, start.plusMinutes(45)));

        timeSlotService.createTimeSlot(doctor.getId(), start, start.plusMinutes(30));
        assertThrows(ValidationException.class,
                () -> timeSlotService.createTimeSlot(doctor.getId(), start.plusMinutes(15), start.plusMinutes(30)));
    }

    @Test
    void patientCannotReadAnotherPatientProfile() throws Exception {
        register("owner@example.com");
        register("other@example.com");
        Long otherId = userRepository.findByEmail("other@example.com").orElseThrow().getId();
        String token = login("owner@example.com");

        mockMvc.perform(get("/api/patients/id/" + otherId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminMustCompleteTwoFactorSetupBeforeUsingProtectedApis() throws Exception {
        User admin = new User();
        admin.setEmail("two-factor-admin@example.com");
        admin.setPassword(passwordEncoder.encode("Admin123!"));
        admin.setUserRole(UserRole.ADMIN);
        admin.setIsActive(true);
        userRepository.save(admin);

        String token = login("two-factor-admin@example.com");
        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    private void register(String email) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("""
                                {"email":"%s","password":"Password1!","firstName":"Test","lastName":"Patient"}
                                """.formatted(email)))
                .andExpect(status().isCreated());
    }

    private String login(String email) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, email.startsWith("two-factor") ? "Admin123!" : "Password1!")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
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
