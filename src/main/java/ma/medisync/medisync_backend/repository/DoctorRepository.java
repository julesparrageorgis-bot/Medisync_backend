package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);
    List<Doctor> findByOfficeId(Long officeId);
    List<Doctor> findByOfficeIsNotNull();
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
}
