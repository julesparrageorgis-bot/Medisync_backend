package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Secretary;
import ma.medisync.medisync_backend.repository.SecretaryRepository;
import ma.medisync.medisync_backend.repository.UserRepository;
import ma.medisync.medisync_backend.entity.enums.UserRole;
import ma.medisync.medisync_backend.util.PasswordUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SecretaryService {

    private final SecretaryRepository secretaryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Secretary createSecretary(Secretary secretary) {
        secretary.getUser().setUserRole(UserRole.SECRETARY);
        secretary.getUser().setIsActive(true);
        PasswordUtil.validatePasswordStrength(secretary.getUser().getPassword());
        secretary.getUser().setPassword(passwordEncoder.encode(secretary.getUser().getPassword()));
        userRepository.save(secretary.getUser());
        return secretaryRepository.save(secretary);
    }

    public Optional<Secretary> getSecretaryById(Long id) {
        return secretaryRepository.findById(id);
    }

    public Optional<Secretary> getSecretaryByUserId(Long userId) {
        return secretaryRepository.findByUserId(userId);
    }

    public List<Secretary> getAllSecretaries() {
        return secretaryRepository.findAll();
    }

    public List<Secretary> getSecretariesByOffice(String officeAssigned) {
        return secretaryRepository.findAll().stream()
                .filter(sec -> sec.getOfficeAssigned() != null && sec.getOfficeAssigned().equals(officeAssigned))
                .toList();
    }

    public Secretary updateSecretary(Long id, Secretary secretaryDetails) {
        Optional<Secretary> secretary = secretaryRepository.findById(id);
        if (secretary.isPresent()) {
            Secretary s = secretary.get();
            s.setOfficeAssigned(secretaryDetails.getOfficeAssigned());
            s.setDepartment(secretaryDetails.getDepartment());
            s.setCanManageAppointments(secretaryDetails.getCanManageAppointments());
            s.setCanManagePatients(secretaryDetails.getCanManagePatients());
            s.setCanGenerateInvoices(secretaryDetails.getCanGenerateInvoices());
            s.setWorkSchedule(secretaryDetails.getWorkSchedule());
            return secretaryRepository.save(s);
        }
        return null;
    }

    public boolean grantAppointmentPermission(Long id) {
        Optional<Secretary> secretary = secretaryRepository.findById(id);
        if (secretary.isPresent()) {
            Secretary s = secretary.get();
            s.setCanManageAppointments(true);
            secretaryRepository.save(s);
            return true;
        }
        return false;
    }

    public boolean revokeAppointmentPermission(Long id) {
        Optional<Secretary> secretary = secretaryRepository.findById(id);
        if (secretary.isPresent()) {
            Secretary s = secretary.get();
            s.setCanManageAppointments(false);
            secretaryRepository.save(s);
            return true;
        }
        return false;
    }

    public boolean grantInvoicePermission(Long id) {
        Optional<Secretary> secretary = secretaryRepository.findById(id);
        if (secretary.isPresent()) {
            Secretary s = secretary.get();
            s.setCanGenerateInvoices(true);
            secretaryRepository.save(s);
            return true;
        }
        return false;
    }

    public boolean deleteSecretary(Long id) {
        if (secretaryRepository.existsById(id)) {
            secretaryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByUserId(Long userId) {
        return secretaryRepository.existsByUserId(userId);
    }
}
