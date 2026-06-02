package ma.medisync.medisync_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class ApiResponses {

    private ApiResponses() {
    }

    public record UserSummary(Long id, String email, String firstName, String lastName, String phone, String role) {
    }

    public record OfficeSummary(Long id, String name, String city) {
    }

    public record OfficeResponse(Long id, String name, String address, String zipCode, String city, String country,
                                 String phone, String email, Boolean active, String description,
                                 LocalDateTime createdAt, LocalDateTime updatedAt) {
    }

    public record MedicationResponse(Long id, String name, String genericName, String description, String dosage,
                                     String form, String manufacturer, String batchNumber, Double price,
                                     Integer stockQuantity, String sideEffects, String contraindications,
                                     Boolean active) {
    }

    public record PatientResponse(Long id, String email, String firstName, String lastName, String phone,
                                  LocalDate dateOfBirth, String gender, String address, String city, String zipCode,
                                  OfficeSummary office) {
    }

    public record DoctorResponse(Long id, String email, String firstName, String lastName, String phone,
                                 String licenseNumber, String specialties, String languages,
                                 BigDecimal consultationRate, OfficeSummary office) {
    }

    public record AppointmentResponse(Long id, Long patientId, Long doctorId, Long dependentId,
                                      Long consultationRoomId, LocalDateTime appointmentDate, String status,
                                      String reason, String notes, Double consultationFee, Integer durationMinutes,
                                      Boolean emergency) {
    }

    public record MedicalRecordResponse(Long id, Long patientId, Long doctorId, Long appointmentId,
                                        String diagnosis, String symptoms, String treatment, String notes,
                                        String recordType, Boolean confidential, LocalDateTime recordDate) {
    }

    public record PrescriptionResponse(Long id, Long patientId, Long doctorId, Long medicalRecordId,
                                       String prescriptionNumber, String medications, String instructions,
                                       String status, LocalDate issueDate, LocalDate expiryDate,
                                       Boolean digitallyFilled) {
    }

    public record MedicalDocumentResponse(Long id, Long patientId, String fileName, String documentType,
                                          String fileType, Long fileSize, String description, Long uploadedById,
                                          Boolean publicDocument, LocalDateTime uploadDate) {
    }

    public record InvoiceResponse(Long id, String invoiceNumber, Long patientId, Long appointmentId,
                                  LocalDate invoiceDate, LocalDate dueDate, Double subtotal, Double taxAmount,
                                  Double discountAmount, Double totalAmount, String status, String paymentMethod,
                                  String notes, Boolean paid, LocalDateTime paidAt) {
    }

    public record InvoiceItemResponse(Long id, Long invoiceId, String description, Integer quantity,
                                      Double unitPrice, Double taxRate, Double totalPrice, String category,
                                      String notes) {
    }

    public record PaymentResponse(Long id, Long invoiceId, BigDecimal amountPaid, String paymentMethod,
                                  LocalDateTime paymentDate) {
    }

    public record PerformedActResponse(Long id, Long appointmentId, String actCode, String description,
                                       BigDecimal amount) {
    }

    public record NotificationResponse(Long id, Long userId, String type, String title, String message,
                                       Boolean read, String channel, Boolean sent, LocalDateTime sentAt,
                                       LocalDateTime readAt, LocalDateTime createdAt, String relatedEntityType,
                                       Long relatedEntityId) {
    }

    public record ReviewResponse(Long id, Long patientId, Long doctorId, Long appointmentId, Integer rating,
                                 String comment, Boolean anonymous, Boolean approved, LocalDateTime createdAt) {
    }

    public record AdminResponse(Long id, UserSummary user, String adminLevel, Boolean can2FA,
                                Boolean twoFactorEnabled, String permissions, Boolean canAccessReports,
                                Boolean canManageUsers, Boolean canManageClinic) {
    }

    public record SecretaryResponse(Long id, UserSummary user, String officeAssigned, String department,
                                    Boolean canManageAppointments, Boolean canManagePatients,
                                    Boolean canGenerateInvoices, String workSchedule) {
    }

    public record AuditLogResponse(Long id, Long userId, String action, String entityType, Long entityId,
                                   String description, String status, String ipAddress, Boolean securityEvent,
                                   LocalDateTime timestamp) {
    }

    public record MonthlyFinancialReportResponse(Long id, String reportMonth, BigDecimal totalRevenue,
                                                 Long unpaidInvoices, Long totalAppointments,
                                                 Double roomOccupancyRate, Double noShowRate,
                                                 LocalDateTime generatedAt) {
    }

    public record ConsultationRoomResponse(Long id, Long officeId, String roomNumber, String roomType,
                                           Integer capacity, String equipment, Boolean active, Boolean available,
                                           String notes) {
    }

    public record EquipmentResponse(Long id, String name, String serialNumber, Long assignedRoomId) {
    }

    public record TariffResponse(Long id, Long doctorId, String actType, String sector, BigDecimal price) {
    }

    public record MedicalReportTemplateResponse(Long id, String name, String content, String specialty) {
    }

    public record IssueReportResponse(Long id, Long reporterId, String title, String description, String status,
                                      LocalDateTime createdAt) {
    }

    public record DependentResponse(Long id, Long parentPatientId, String firstName, String lastName,
                                    LocalDate birthDate, String relationship) {
    }

    public record DoctorUnavailabilityResponse(Long id, Long doctorId, LocalDateTime startTime,
                                               LocalDateTime endTime, String reason, Boolean leaveDay) {
    }
}
