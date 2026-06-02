package ma.medisync.medisync_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.medisync.medisync_backend.entity.*;
import ma.medisync.medisync_backend.entity.enums.UserRole;
import ma.medisync.medisync_backend.exception.FileStorageException;
import ma.medisync.medisync_backend.exception.ValidationException;
import ma.medisync.medisync_backend.repository.*;
import ma.medisync.medisync_backend.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "file.upload-dir=/tmp/medisync-test-uploads")
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class WebIntegrationHardeningTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private PatientRepository patientRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private TimeSlotRepository timeSlotRepository;
    @Autowired private AppointmentService appointmentService;
    @Autowired private PrescriptionRepository prescriptionRepository;
    @Autowired private InvoiceRepository invoiceRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private DoctorUnavailabilityRepository unavailabilityRepository;
    @Autowired private DependentRepository dependentRepository;
    @Autowired private TimeSlotService timeSlotService;
    @Autowired private DocumentStorageService storageService;
    @Autowired private DashboardService dashboardService;
    @Autowired private MonthlyFinancialReportService monthlyReportService;

    @Test
    void patientProfileResponseUsesSafeDto() throws Exception {
        Patient patient = patient("dto-patient@example.com");
        String token = token(patient.getEmail());

        mockMvc.perform(get("/api/patients/id/" + patient.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patient.getId()))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.socialSecurityNumber").doesNotExist())
                .andExpect(jsonPath("$.authorities").doesNotExist());
    }

    @Test
    void prescriptionPdfAndInvoicePdfDownloadForOwningPatient() throws Exception {
        Patient patient = patient("download-owner@example.com");
        Doctor doctor = doctor("download-doctor@example.com");
        Prescription prescription = new Prescription();
        prescription.setPatient(patient);
        prescription.setDoctor(doctor);
        prescription.setPrescriptionNumber("RX-" + System.nanoTime());
        prescription.setMedications("Paracetamol 500mg");
        prescription.setInstructions("Twice daily");
        prescription.setIssueDate(LocalDate.now());
        prescription.setExpiryDate(LocalDate.now().plusDays(30));
        prescription = prescriptionRepository.save(prescription);

        Invoice invoice = invoiceRepository.save(Invoice.builder()
                .invoiceNumber("INV-" + System.nanoTime())
                .patient(patient)
                .invoiceDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(15))
                .totalAmount(250.0)
                .build());

        String token = token(patient.getEmail());
        mockMvc.perform(get("/api/prescriptions/id/" + prescription.getId() + "/pdf")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));
        mockMvc.perform(get("/api/invoices/" + invoice.getId() + "/pdf")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));
    }

    @Test
    void documentStorageRejectsNonClinicalFileTypes() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "malware.exe", "application/octet-stream", new byte[]{1, 2, 3});
        assertThrows(FileStorageException.class, () -> storageService.storeFile(file));
    }

    @Test
    void owningPatientCanUploadListAndDownloadClinicalDocument() throws Exception {
        Patient patient = patient("document-owner@example.com");
        String token = token(patient.getEmail());
        MockMultipartFile file = new MockMultipartFile(
                "file", "result.pdf", "application/pdf", "%PDF-1.4 test".getBytes());

        String response = mockMvc.perform(multipart("/api/documents/upload")
                        .file(file)
                        .param("patientId", patient.getId().toString())
                        .param("documentType", "LAB_REPORT")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patientId").value(patient.getId()))
                .andExpect(jsonPath("$.filePath").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        long documentId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/documents/patient/" + patient.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(patient.getId()));
        mockMvc.perform(get("/api/documents/download/" + documentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void notificationResponseUsesSafeDto() throws Exception {
        Patient patient = patient("notification-owner@example.com");
        notificationRepository.save(Notification.builder()
                .user(patient).type("GENERAL").title("Hello").message("Your notification").build());

        mockMvc.perform(get("/api/notifications").header("Authorization", "Bearer " + token(patient.getEmail())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(patient.getId()))
                .andExpect(jsonPath("$[0].user").doesNotExist());
    }

    @Test
    void unrelatedPatientDoctorAndSecretaryCannotReadMedicalRecords() throws Exception {
        Patient owner = patient("medical-owner@example.com");
        Patient unrelatedPatient = patient("medical-unrelated@example.com");
        Doctor unrelatedDoctor = doctor("medical-unrelated-doctor@example.com");
        User secretary = user(UserRole.SECRETARY, "medical-secretary@example.com");

        for (String email : java.util.List.of(unrelatedPatient.getEmail(), unrelatedDoctor.getEmail(), secretary.getEmail())) {
            mockMvc.perform(get("/api/medical-records/patient/" + owner.getId())
                            .header("Authorization", "Bearer " + token(email)))
                    .andExpect(status().isForbidden());
        }
    }

    @Test
    void leavePeriodPreventsCreatingDoctorSlot() {
        Doctor doctor = doctor("leave-doctor@example.com");
        LocalDateTime start = LocalDateTime.now().plusDays(10).withNano(0);
        DoctorUnavailability leave = new DoctorUnavailability();
        leave.setDoctor(doctor);
        leave.setStartTime(start);
        leave.setEndTime(start.plusHours(8));
        leave.setLeaveDay(true);
        unavailabilityRepository.save(leave);

        assertThrows(ValidationException.class,
                () -> timeSlotService.createTimeSlot(doctor.getId(), start.plusHours(1), start.plusHours(1).plusMinutes(30)));
    }

    @Test
    void dependentBookingPersistsRelationship() {
        Patient patient = patient("dependent-parent@example.com");
        Doctor doctor = doctor("dependent-doctor@example.com");
        Dependent dependent = new Dependent();
        dependent.setParentPatient(patient);
        dependent.setFirstName("Child");
        dependent.setLastName("Patient");
        dependent.setRelationship("child");
        dependent = dependentRepository.save(dependent);
        LocalDateTime start = LocalDateTime.now().plusDays(12).withNano(0);
        timeSlotRepository.save(TimeSlot.builder().doctor(doctor).startTime(start).endTime(start.plusMinutes(30)).build());

        Appointment appointment = Appointment.builder()
                .patient(patient).doctor(doctor).dependent(dependent).appointmentDate(start).build();
        Appointment saved = appointmentService.createAppointment(appointment);
        assertEquals(dependent.getId(), saved.getDependent().getId());
    }

    @Test
    void dashboardAndMonthlySnapshotRemainFinite() {
        assertTrue(Double.isFinite(dashboardService.getStats().getOccupancyRate()));
        assertTrue(Double.isFinite(dashboardService.getStats().getNoShowRate()));
        var report = monthlyReportService.generate(YearMonth.of(2026, 6));
        assertEquals("2026-06", report.reportMonth());
        assertFalse(monthlyReportService.findAll().isEmpty());
    }

    @Test
    @WithMockUser(username = "report-admin@example.com", roles = "ADMIN")
    void adminExportsAndAuditDtosWork() throws Exception {
        User user = patient("audit-patient@example.com");
        auditLogRepository.save(AuditLog.builder()
                .user(user).action("ACCESS").entityType("Patient").entityId(user.getId())
                .status("SUCCESS").build());

        mockMvc.perform(get("/api/admin/reports/financial.pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));
        mockMvc.perform(get("/api/admin/reports/financial.xlsx"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        var report = monthlyReportService.generate(YearMonth.of(2026, 5));
        mockMvc.perform(get("/api/admin/reports/monthly/" + report.id() + ".pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));
        mockMvc.perform(get("/api/admin/reports/monthly/" + report.id() + ".xlsx"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        mockMvc.perform(get("/api/audit-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user").doesNotExist())
                .andExpect(jsonPath("$[0].userId").exists());
    }

    private Patient patient(String email) {
        Patient patient = new Patient();
        patient.setEmail(email);
        patient.setPassword(passwordEncoder.encode("Password1!"));
        patient.setUserRole(UserRole.PATIENT);
        patient.setIsActive(true);
        return patientRepository.save(patient);
    }

    private Doctor doctor(String email) {
        Doctor doctor = new Doctor();
        doctor.setEmail(email);
        doctor.setPassword(passwordEncoder.encode("Password1!"));
        doctor.setUserRole(UserRole.DOCTOR);
        doctor.setIsActive(true);
        doctor.setLicenseNumber("LICENSE-" + System.nanoTime());
        return doctorRepository.save(doctor);
    }

    private User user(UserRole role, String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("Password1!"));
        user.setUserRole(role);
        user.setIsActive(true);
        return userRepository.save(user);
    }

    private String token(String email) throws Exception {
        String response = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/auth/login").contentType("application/json")
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "email", email, "password", "Password1!"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }
}
