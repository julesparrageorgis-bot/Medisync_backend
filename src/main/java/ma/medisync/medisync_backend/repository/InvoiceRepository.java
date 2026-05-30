package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByPatientId(Long patientId);
    List<Invoice> findByStatus(String status);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByInvoiceDateBetween(LocalDate start, LocalDate end);
    List<Invoice> findByPatientIdAndStatus(Long patientId, String status);
    List<Invoice> findByIsPaidFalseAndDueDateBefore(LocalDate date);
}
