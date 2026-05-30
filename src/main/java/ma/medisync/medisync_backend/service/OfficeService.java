package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Office;
import ma.medisync.medisync_backend.repository.OfficeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OfficeService {

    private final OfficeRepository officeRepository;

    public Office createOffice(Office office) {
        return officeRepository.save(office);
    }

    public Optional<Office> getOfficeById(Long id) {
        return officeRepository.findById(id);
    }

    public List<Office> getAllOffices() {
        return officeRepository.findAll();
    }

    public Office updateOffice(Long id, Office officeDetails) {
        Optional<Office> office = officeRepository.findById(id);
        if (office.isPresent()) {
            Office o = office.get();
            o.setName(officeDetails.getName());
            o.setAddress(officeDetails.getAddress());
            o.setCity(officeDetails.getCity());
            o.setZipCode(officeDetails.getZipCode());
            o.setPhone(officeDetails.getPhone());
            o.setEmail(officeDetails.getEmail());
            o.setDescription(officeDetails.getDescription());
            o.setIsActive(officeDetails.getIsActive());
            return officeRepository.save(o);
        }
        return null;
    }

    public boolean deleteOffice(Long id) {
        if (officeRepository.existsById(id)) {
            officeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Office> getOfficesByCity(String city) {
        return officeRepository.findAll().stream()
                .filter(o -> o.getCity() != null && o.getCity().equals(city))
                .toList();
    }

    public Optional<Office> getOfficeByName(String name) {
        return officeRepository.findAll().stream()
                .filter(o -> o.getName().equals(name))
                .findFirst();
    }
}
