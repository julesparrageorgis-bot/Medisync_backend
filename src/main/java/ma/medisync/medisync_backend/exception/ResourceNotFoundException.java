package ma.medisync.medisync_backend.exception;

public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ResourceNotFoundException userNotFound(Long id) {
        return new ResourceNotFoundException("User not found with id: " + id);
    }

    public static ResourceNotFoundException userNotFoundByEmail(String email) {
        return new ResourceNotFoundException("User not found with email: " + email);
    }

    public static ResourceNotFoundException patientNotFound(Long id) {
        return new ResourceNotFoundException("Patient not found with id: " + id);
    }

    public static ResourceNotFoundException doctorNotFound(Long id) {
        return new ResourceNotFoundException("Doctor not found with id: " + id);
    }

    public static ResourceNotFoundException appointmentNotFound(Long id) {
        return new ResourceNotFoundException("Appointment not found with id: " + id);
    }

    public static ResourceNotFoundException recordNotFound(Long id) {
        return new ResourceNotFoundException("Medical record not found with id: " + id);
    }

    public static ResourceNotFoundException officeNotFound(Long id) {
        return new ResourceNotFoundException("Office not found with id: " + id);
    }

    public static ResourceNotFoundException invoiceNotFound(Long id) {
        return new ResourceNotFoundException("Invoice not found with id: " + id);
    }

    public static ResourceNotFoundException prescriptionNotFound(Long id) {
        return new ResourceNotFoundException("Prescription not found with id: " + id);
    }

    public static ResourceNotFoundException documentNotFound(Long id) {
        return new ResourceNotFoundException("Document not found with id: " + id);
    }
}
