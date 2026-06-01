package ma.medisync.medisync_backend.exception;

public class UnauthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public static UnauthorizedException invalidToken() {
        return new UnauthorizedException("Invalid or expired token");
    }

    public static UnauthorizedException tokenExpired() {
        return new UnauthorizedException("Token has expired");
    }

    public static UnauthorizedException insufficientPermissions() {
        return new UnauthorizedException("Insufficient permissions to perform this action");
    }

    public static UnauthorizedException invalidCredentials() {
        return new UnauthorizedException("Invalid credentials");
    }

    public static UnauthorizedException invalidTwoFactorCode() {
        return new UnauthorizedException("Invalid two-factor authentication code");
    }
}
