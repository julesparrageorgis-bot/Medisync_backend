package com.medisync.repository;

import com.medisync.entity.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TariffRepository extends JpaRepository<Tariff, Long> {
    List<Tariff> findByDoctorId(Long doctorId);
}