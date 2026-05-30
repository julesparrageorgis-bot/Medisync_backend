package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Doctor;
import ma.medisync.medisync_backend.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public Optional<Doctor> getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserId(userId);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    public List<Doctor> getAvailableDoctors() {
        return doctorRepository.findByIsAvailableTrue();
    }

    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findAll().stream()
                .filter(doc -> doc.getSpecialization() != null && doc.getSpecialization().equals(specialty))
                .toList();
    }

    public List<Doctor> getDoctorsByOffice(Long officeId) {
        // Return empty list for now - Doctor doesn't have officeId field
        return doctorRepository.findAll().stream()
                .limit(0)
                .toList();
    }

    public Doctor updateDoctor(Long id, Doctor doctorDetails) {
        Optional<Doctor> doctor = doctorRepository.findById(id);
        if (doctor.isPresent()) {
            Doctor d = doctor.get();
            d.setSpecialization(doctorDetails.getSpecialization());
            d.setLicenseNumber(doctorDetails.getLicenseNumber());
            d.setConsultationFee(doctorDetails.getConsultationFee());
            d.setYearsOfExperience(doctorDetails.getYearsOfExperience());
            d.setBio(doctorDetails.getBio());
            d.setIsAvailable(doctorDetails.getIsAvailable());
            d.setOfficeLocation(doctorDetails.getOfficeLocation());
            return doctorRepository.save(d);
        }
        return null;
    }

    public Doctor setAvailability(Long id, boolean available) {
        Optional<Doctor> doctor = doctorRepository.findById(id);
        if (doctor.isPresent()) {
            Doctor d = doctor.get();
            d.setIsAvailable(available);
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

    public boolean existsByUserId(Long userId) {
        return doctorRepository.existsByUserId(userId);
    }
}
