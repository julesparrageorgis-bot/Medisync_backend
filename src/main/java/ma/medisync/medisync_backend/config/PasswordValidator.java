package ma.medisync.medisync_backend.config;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class PasswordValidator {
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";

    public boolean isValid(String password) {
        return Pattern.matches(PASSWORD_PATTERN, password);
    }
}