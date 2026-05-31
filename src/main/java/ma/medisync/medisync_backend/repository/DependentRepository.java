package com.medisync.repository;

import com.medisync.entity.Dependent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DependentRepository extends JpaRepository<Dependent, Long> {
    List<Dependent> findByParentPatientId(Long parentPatientId);
}