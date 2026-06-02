package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Appointment;
import ma.medisync.medisync_backend.entity.User;
import ma.medisync.medisync_backend.entity.enums.UserRole;
import ma.medisync.medisync_backend.repository.AppointmentRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AppointmentRepository appointmentRepository;

    public User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user;
        }
        throw new AccessDeniedException("Authenticated user is required");
    }

    public boolean hasRole(UserRole role) {
        return currentUser().getUserRole() == role;
    }

    public void assertCanAccessPatientProfile(Long patientId) {
        User user = currentUser();
        if (user.getUserRole() == UserRole.PATIENT && !user.getId().equals(patientId)) {
            throw new AccessDeniedException("Patients can only access their own profile");
        }
        if (user.getUserRole() == UserRole.DOCTOR && !hasCareRelationship(user.getId(), patientId)) {
            throw new AccessDeniedException("Doctor has no care relationship with this patient");
        }
    }

    public void assertCanAccessMedicalData(Long patientId) {
        User user = currentUser();
        if (user.getUserRole() == UserRole.ADMIN) {
            return;
        }
        if (user.getUserRole() == UserRole.PATIENT && user.getId().equals(patientId)) {
            return;
        }
        if (user.getUserRole() == UserRole.DOCTOR && hasCareRelationship(user.getId(), patientId)) {
            return;
        }
        throw new AccessDeniedException("Medical data access is not allowed");
    }

    public void assertCanAccessAppointment(Appointment appointment) {
        User user = currentUser();
        if (user.getUserRole() == UserRole.PATIENT &&
                !user.getId().equals(appointment.getPatient().getId())) {
            throw new AccessDeniedException("Patients can only access their own appointments");
        }
        if (user.getUserRole() == UserRole.DOCTOR &&
                !user.getId().equals(appointment.getDoctor().getId())) {
            throw new AccessDeniedException("Doctors can only access their own appointments");
        }
    }

    public boolean canAccessAppointment(Appointment appointment) {
        try {
            assertCanAccessAppointment(appointment);
            return true;
        } catch (AccessDeniedException ex) {
            return false;
        }
    }

    public void assertDoctorSelf(Long doctorId) {
        if (hasRole(UserRole.DOCTOR) && !currentUser().getId().equals(doctorId)) {
            throw new AccessDeniedException("Doctors can only access their own resources");
        }
    }

    private boolean hasCareRelationship(Long doctorId, Long patientId) {
        return appointmentRepository.existsByDoctorIdAndPatientId(doctorId, patientId);
    }
}
