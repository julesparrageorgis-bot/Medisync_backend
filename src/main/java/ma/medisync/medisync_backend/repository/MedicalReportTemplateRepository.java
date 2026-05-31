package com.medisync.repository;

import com.medisync.entity.MedicalReportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalReportTemplateRepository extends JpaRepository<MedicalReportTemplate, Long> {
}