package ma.medisync.medisync_backend.exception;

public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ValidationException invalidDateRange() {
        return new ValidationException("Invalid date range: start date must be before end date");
    }

    public static ValidationException invalidAppointmentTime() {
        return new ValidationException("Appointment time must be in the future");
    }

    public static ValidationException slotNotAvailable() {
        return new ValidationException("Selected time slot is not available");
    }

    public static ValidationException doctorNotAvailable() {
        return new ValidationException("Doctor is not available at the requested time");
    }

    public static ValidationException invalidFileFormat() {
        return new ValidationException("Invalid file format");
    }
}
