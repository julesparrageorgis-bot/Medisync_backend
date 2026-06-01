package ma.medisync.medisync_backend.exception;

public class DuplicateResourceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public static DuplicateResourceException emailAlreadyExists(String email) {
        return new DuplicateResourceException("Email already exists: " + email);
    }

    public static DuplicateResourceException ssnAlreadyExists(String ssn) {
        return new DuplicateResourceException("Social security number already exists: " + ssn);
    }

    public static DuplicateResourceException licenseNumberAlreadyExists(String license) {
        return new DuplicateResourceException("License number already exists: " + license);
    }

    public static DuplicateResourceException invoiceNumberAlreadyExists(String invoiceNumber) {
        return new DuplicateResourceException("Invoice number already exists: " + invoiceNumber);
    }
}
