package ma.medisync.medisync_backend.util;

import ma.medisync.medisync_backend.exception.InvalidPasswordException;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
public class PasswordUtil {
    
    // Password must be at least 8 characters, contain uppercase, lowercase, digit, and special character
    private static final String PASSWORD_REGEX = 
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    
    public static void validatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            throw InvalidPasswordException.weakPassword();
        }
        
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw InvalidPasswordException.weakPassword();
        }
    }
    
    public static boolean isStrongPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    public static String getPasswordRequirements() {
        return "Password must be at least 8 characters and contain uppercase letter, lowercase letter, digit, and special character";
    }
}
