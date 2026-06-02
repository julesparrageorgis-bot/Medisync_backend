package ma.medisync.medisync_backend.service;

import ma.medisync.medisync_backend.dto.ApiResponses.*;
import ma.medisync.medisync_backend.entity.*;
import org.springframework.stereotype.Component;

@Component
public class ApiResponseMapper {

    public PatientResponse patient(Patient value) {
        return new PatientResponse(value.getId(), value.getEmail(), value.getFirstName(), value.getLastName(),
                value.getPhone(), value.getDateOfBirth(), enumName(value.getGender()), value.getAddress(),
                value.getCity(), value.getZipCode(), office(value.getOffice()));
    }

    public DoctorResponse doctor(Doctor value) {
        return new DoctorResponse(value.getId(), value.getEmail(), value.getFirstName(), value.getLastName(),
                value.getPhone(), value.getLicenseNumber(), value.getSpecialties(), value.getLanguages(),
                value.getConsultationRate(), office(value.getOffice()));
    }

    public OfficeResponse officeResponse(Office value) {
        return new OfficeResponse(value.getId(), value.getName(), value.getAddress(), value.getZipCode(),
                value.getCity(), value.getCountry(), value.getPhone(), value.getEmail(), value.getIsActive(),
                value.getDescription(), value.getCreatedAt(), value.getUpdatedAt());
    }

    public MedicationResponse medication(Medication value) {
        return new MedicationResponse(value.getId(), value.getName(), value.getGenericName(), value.getDescription(),
                value.getDosage(), value.getForm(), value.getManufacturer(), value.getBatchNumber(), value.getPrice(),
                value.getStockQuantity(), value.getSideEffects(), value.getContraindications(), value.getIsActive());
    }

    public AppointmentResponse appointment(Appointment value) {
        return new AppointmentResponse(value.getId(), id(value.getPatient()), id(value.getDoctor()),
                id(value.getDependent()), id(value.getConsultationRoom()), value.getAppointmentDate(),
                value.getStatus(), value.getReason(), value.getNotes(), value.getConsultationFee(),
                value.getDurationMinutes(), value.getEmergency());
    }

    public MedicalRecordResponse record(MedicalRecord value) {
        return new MedicalRecordResponse(value.getId(), id(value.getPatient()), id(value.getDoctor()),
                id(value.getAppointment()), value.getDiagnosis(), value.getSymptoms(), value.getTreatment(),
                value.getNotes(), value.getRecordType(), value.getIsConfidential(), value.getRecordDate());
    }

    public PrescriptionResponse prescription(Prescription value) {
        return new PrescriptionResponse(value.getId(), id(value.getPatient()), id(value.getDoctor()),
                id(value.getMedicalRecord()), value.getPrescriptionNumber(), value.getMedications(),
                value.getInstructions(), value.getStatus(), value.getIssueDate(), value.getExpiryDate(),
                value.getIsDigitallyFilled());
    }

    public MedicalDocumentResponse document(MedicalDocument value) {
        return new MedicalDocumentResponse(value.getId(), id(value.getPatient()), value.getFileName(),
                value.getDocumentType(), value.getFileType(), value.getFileSize(), value.getDescription(),
                id(value.getUploadedBy()), value.getIsPublic(), value.getUploadDate());
    }

    public InvoiceResponse invoice(Invoice value) {
        return new InvoiceResponse(value.getId(), value.getInvoiceNumber(), id(value.getPatient()),
                id(value.getAppointment()), value.getInvoiceDate(), value.getDueDate(), value.getSubtotal(),
                value.getTaxAmount(), value.getDiscountAmount(), value.getTotalAmount(), value.getStatus(),
                value.getPaymentMethod(), value.getNotes(), value.getIsPaid(), value.getPaidAt());
    }

    public InvoiceItemResponse invoiceItem(InvoiceItem value) {
        return new InvoiceItemResponse(value.getId(), id(value.getInvoice()), value.getDescription(),
                value.getQuantity(), value.getUnitPrice(), value.getTaxRate(), value.getTotalPrice(),
                value.getCategory(), value.getNotes());
    }

    public PaymentResponse payment(Payment value) {
        return new PaymentResponse(value.getId(), id(value.getInvoice()), value.getAmountPaid(),
                value.getPaymentMethod(), value.getPaymentDate());
    }

    public PerformedActResponse performedAct(PerformedAct value) {
        return new PerformedActResponse(value.getId(), id(value.getAppointment()), value.getActCode(),
                value.getDescription(), value.getAmount());
    }

    public NotificationResponse notification(Notification value) {
        return new NotificationResponse(value.getId(), id(value.getUser()), value.getType(), value.getTitle(),
                value.getMessage(), value.getIsRead(), value.getNotificationChannel(), value.getIsSent(),
                value.getSentAt(), value.getReadAt(), value.getCreatedAt(), value.getRelatedEntityType(),
                value.getRelatedEntityId());
    }

    public ReviewResponse review(Review value) {
        return new ReviewResponse(value.getId(), id(value.getPatient()), id(value.getDoctor()),
                id(value.getAppointment()), value.getRating(), value.getComment(), value.getIsAnonymous(),
                value.getIsApproved(), value.getCreatedAt());
    }

    public AdminResponse admin(Admin value) {
        return new AdminResponse(value.getId(), user(value.getUser()), value.getAdminLevel(), value.getCan2FA(),
                value.getTwoFactorEnabled(), value.getPermissions(), value.getCanAccessReports(),
                value.getCanManageUsers(), value.getCanManageClinic());
    }

    public SecretaryResponse secretary(Secretary value) {
        return new SecretaryResponse(value.getId(), user(value.getUser()), value.getOfficeAssigned(),
                value.getDepartment(), value.getCanManageAppointments(), value.getCanManagePatients(),
                value.getCanGenerateInvoices(), value.getWorkSchedule());
    }

    public AuditLogResponse auditLog(AuditLog value) {
        return new AuditLogResponse(value.getId(), id(value.getUser()), value.getAction(), value.getEntityType(),
                value.getEntityId(), value.getDescription(), value.getStatus(), value.getIpAddress(),
                value.getIsSecurityEvent(), value.getTimestamp());
    }

    public ConsultationRoomResponse room(ConsultationRoom value) {
        return new ConsultationRoomResponse(value.getId(), id(value.getOffice()), value.getRoomNumber(),
                value.getRoomType(), value.getCapacity(), value.getEquipment(), value.getIsActive(),
                value.getIsAvailable(), value.getNotes());
    }

    public EquipmentResponse equipment(Equipment value) {
        return new EquipmentResponse(value.getId(), value.getName(), value.getSerialNumber(),
                id(value.getAssignedRoom()));
    }

    public TariffResponse tariff(Tariff value) {
        return new TariffResponse(value.getId(), id(value.getDoctor()), value.getActType(), value.getSector(),
                value.getPrice());
    }

    public MedicalReportTemplateResponse template(MedicalReportTemplate value) {
        return new MedicalReportTemplateResponse(value.getId(), value.getName(), value.getContent(),
                value.getSpecialty());
    }

    public IssueReportResponse issue(IssueReport value) {
        return new IssueReportResponse(value.getId(), id(value.getReporter()), value.getTitle(),
                value.getDescription(), value.getStatus(), value.getCreatedAt());
    }

    public DependentResponse dependent(Dependent value) {
        return new DependentResponse(value.getId(), id(value.getParentPatient()), value.getFirstName(),
                value.getLastName(), value.getBirthDate(), value.getRelationship());
    }

    public DoctorUnavailabilityResponse unavailability(DoctorUnavailability value) {
        return new DoctorUnavailabilityResponse(value.getId(), id(value.getDoctor()), value.getStartTime(),
                value.getEndTime(), value.getReason(), value.isLeaveDay());
    }

    private UserSummary user(User value) {
        return value == null ? null : new UserSummary(value.getId(), value.getEmail(), value.getFirstName(),
                value.getLastName(), value.getPhone(), enumName(value.getUserRole()));
    }

    private OfficeSummary office(Office value) {
        return value == null ? null : new OfficeSummary(value.getId(), value.getName(), value.getCity());
    }

    private Long id(Object value) {
        if (value instanceof User entity) return entity.getId();
        if (value instanceof Appointment entity) return entity.getId();
        if (value instanceof MedicalRecord entity) return entity.getId();
        if (value instanceof MedicalDocument entity) return entity.getId();
        if (value instanceof Invoice entity) return entity.getId();
        if (value instanceof Dependent entity) return entity.getId();
        if (value instanceof ConsultationRoom entity) return entity.getId();
        if (value instanceof Office entity) return entity.getId();
        if (value instanceof Room entity) return entity.getId();
        return null;
    }

    private String enumName(Enum<?> value) {
        return value == null ? null : value.name();
    }
}
