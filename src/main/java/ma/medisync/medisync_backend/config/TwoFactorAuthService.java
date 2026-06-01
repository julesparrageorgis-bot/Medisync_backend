package ma.medisync.medisync_backend.config;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorAuthService {
    @Value("${totp.issuer}")
    private String issuer;

    private final DefaultSecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final TimeProvider timeProvider = new SystemTimeProvider();
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator();
    private final CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
    private final QrGenerator qrGenerator = new ZxingPngQrGenerator();

    public String generateSecret() {
        return secretGenerator.generate();
    }

    public String getQrUri(String secret, String email) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", issuer, email, secret, issuer);
    }

    public byte[] generateQrCode(String secret, String email) throws Exception {
        QrData data = new QrData.Builder()
                .label(email)
                .secret(secret)
                .issuer(issuer)
                .build();
        return qrGenerator.generate(data);
    }

    public boolean verifyCode(String secret, String code) {
        return verifier.isValidCode(secret, code);
    }
}