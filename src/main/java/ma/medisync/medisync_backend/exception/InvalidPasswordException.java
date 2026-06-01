package ma.medisync.medisync_backend.exception;

public class InvalidPasswordException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidPasswordException weakPassword() {
        return new InvalidPasswordException("Password must be at least 8 characters long and contain uppercase, lowercase, digit, and special character");
    }

    public static InvalidPasswordException incorrectPassword() {
        return new InvalidPasswordException("Incorrect current password");
    }
}
