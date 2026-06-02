package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Doctor;
import ma.medisync.medisync_backend.repository.DoctorRepository;
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
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    public Doctor createDoctor(Doctor doctor) {
        doctor.setUserRole(UserRole.DOCTOR);
        doctor.setIsActive(true);
        PasswordUtil.validatePasswordStrength(doctor.getPassword());
        doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
        return doctorRepository.save(doctor);
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findAll().stream()
                .filter(doc -> doc.getSpecialties() != null && doc.getSpecialties().contains(specialty))
                .toList();
    }

    public List<Doctor> getAvailableDoctors() {
        return doctorRepository.findByOfficeIsNotNull();
    }

    public List<Doctor> getDoctorsByOffice(Long officeId) {
        return doctorRepository.findByOfficeId(officeId);
    }

    public Doctor updateDoctor(Long id, Doctor doctorDetails) {
        Optional<Doctor> doctor = doctorRepository.findById(id);
        if (doctor.isPresent()) {
            Doctor d = doctor.get();
            if (doctorDetails.getSpecialties() != null) {
                d.setSpecialties(doctorDetails.getSpecialties());
            }
            if (doctorDetails.getLicenseNumber() != null) {
                d.setLicenseNumber(doctorDetails.getLicenseNumber());
            }
            if (doctorDetails.getConsultationRate() != null) {
                d.setConsultationRate(doctorDetails.getConsultationRate());
            }
            if (doctorDetails.getLanguages() != null) {
                d.setLanguages(doctorDetails.getLanguages());
            }
            if (doctorDetails.getOffice() != null) {
                d.setOffice(doctorDetails.getOffice());
            }
            return doctorRepository.save(d);
        }
        return null;
    }

    public boolean deleteDoctor(Long id) {
        if (doctorRepository.existsById(id)) {
            doctorRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
