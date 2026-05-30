package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Invoice;
import ma.medisync.medisync_backend.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public Invoice createInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    public Optional<Invoice> getInvoiceByNumber(String number) {
        return invoiceRepository.findByInvoiceNumber(number);
    }

    public List<Invoice> getPatientInvoices(Long patientId) {
        return invoiceRepository.findByPatientId(patientId);
    }

    public List<Invoice> getInvoicesByStatus(String status) {
        return invoiceRepository.findByStatus(status);
    }

    public List<Invoice> getPatientPendingInvoices(Long patientId) {
        return invoiceRepository.findByPatientIdAndStatus(patientId, "PENDING");
    }

    public List<Invoice> getOverdueInvoices() {
        return invoiceRepository.findByIsPaidFalseAndDueDateBefore(LocalDate.now());
    }

    public List<Invoice> getInvoicesByDateRange(LocalDate start, LocalDate end) {
        return invoiceRepository.findByInvoiceDateBetween(start, end);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice updateInvoice(Long id, Invoice invoiceDetails) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (invoice.isPresent()) {
            Invoice i = invoice.get();
            i.setStatus(invoiceDetails.getStatus());
            i.setPaymentMethod(invoiceDetails.getPaymentMethod());
            i.setSubtotal(invoiceDetails.getSubtotal());
            i.setTaxAmount(invoiceDetails.getTaxAmount());
            i.setDiscountAmount(invoiceDetails.getDiscountAmount());
            i.setTotalAmount(invoiceDetails.getTotalAmount());
            return invoiceRepository.save(i);
        }
        return null;
    }

    public Invoice markAsPaid(Long id) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (invoice.isPresent()) {
            Invoice i = invoice.get();
            i.setIsPaid(true);
            i.setPaidAt(LocalDateTime.now());
            i.setStatus("PAID");
            return invoiceRepository.save(i);
        }
        return null;
    }

    public boolean deleteInvoice(Long id) {
        if (invoiceRepository.existsById(id)) {
            invoiceRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
