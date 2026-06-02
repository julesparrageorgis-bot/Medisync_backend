package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Admin;
import ma.medisync.medisync_backend.repository.AdminRepository;
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
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Admin createAdmin(Admin admin) {
        admin.getUser().setUserRole(UserRole.ADMIN);
        admin.getUser().setIsActive(true);
        PasswordUtil.validatePasswordStrength(admin.getUser().getPassword());
        admin.getUser().setPassword(passwordEncoder.encode(admin.getUser().getPassword()));
        userRepository.save(admin.getUser());
        return adminRepository.save(admin);
    }

    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }

    public Optional<Admin> getAdminByUserId(Long userId) {
        return adminRepository.findByUserId(userId);
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Admin updateAdmin(Long id, Admin adminDetails) {
        Optional<Admin> admin = adminRepository.findById(id);
        if (admin.isPresent()) {
            Admin a = admin.get();
            a.setAdminLevel(adminDetails.getAdminLevel());
            a.setCanManageUsers(adminDetails.getCanManageUsers());
            a.setCanAccessReports(adminDetails.getCanAccessReports());
            a.setCanManageClinic(adminDetails.getCanManageClinic());
            a.setTwoFactorEnabled(adminDetails.getTwoFactorEnabled());
            a.setPermissions(adminDetails.getPermissions());
            return adminRepository.save(a);
        }
        return null;
    }

    public boolean grantUserManagementPermission(Long id) {
        Optional<Admin> admin = adminRepository.findById(id);
        if (admin.isPresent()) {
            Admin a = admin.get();
            a.setCanManageUsers(true);
            adminRepository.save(a);
            return true;
        }
        return false;
    }

    public boolean grantClinicManagementPermission(Long id) {
        Optional<Admin> admin = adminRepository.findById(id);
        if (admin.isPresent()) {
            Admin a = admin.get();
            a.setCanManageClinic(true);
            adminRepository.save(a);
            return true;
        }
        return false;
    }

    public boolean grantReportPermission(Long id) {
        Optional<Admin> admin = adminRepository.findById(id);
        if (admin.isPresent()) {
            Admin a = admin.get();
            a.setCanAccessReports(true);
            adminRepository.save(a);
            return true;
        }
        return false;
    }

    public boolean enableTwoFactor(Long id) {
        Optional<Admin> admin = adminRepository.findById(id);
        if (admin.isPresent()) {
            Admin a = admin.get();
            a.setTwoFactorEnabled(true);
            adminRepository.save(a);
            return true;
        }
        return false;
    }

    public boolean disableTwoFactor(Long id) {
        Optional<Admin> admin = adminRepository.findById(id);
        if (admin.isPresent()) {
            Admin a = admin.get();
            a.setTwoFactorEnabled(false);
            adminRepository.save(a);
            return true;
        }
        return false;
    }

    public boolean deleteAdmin(Long id) {
        if (adminRepository.existsById(id)) {
            adminRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByUserId(Long userId) {
        return adminRepository.existsByUserId(userId);
    }

    public List<Admin> getAdminsByLevel(String adminLevel) {
        return adminRepository.findAll().stream()
                .filter(admin -> admin.getAdminLevel() != null && admin.getAdminLevel().equals(adminLevel))
                .toList();
    }

    public List<Admin> getAdminsWithTwoFactor() {
        return adminRepository.findAll().stream()
                .filter(Admin::getTwoFactorEnabled)
                .toList();
    }

    public long getTotalAdminsCount() {
        return adminRepository.count();
    }

    public boolean grantPermission(Long id, String permission) {
        Optional<Admin> admin = adminRepository.findById(id);
        if (admin.isPresent()) {
            Admin a = admin.get();
            // Simplified: just set permissions as string
            a.setPermissions(permission);
            adminRepository.save(a);
            return true;
        }
        return false;
    }
}
