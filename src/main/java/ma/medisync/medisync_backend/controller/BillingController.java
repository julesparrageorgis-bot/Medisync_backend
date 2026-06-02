package ma.medisync.medisync_backend.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.Invoice;
import ma.medisync.medisync_backend.entity.InvoiceItem;
import ma.medisync.medisync_backend.entity.Payment;
import ma.medisync.medisync_backend.entity.PerformedAct;
import ma.medisync.medisync_backend.exception.ResourceNotFoundException;
import ma.medisync.medisync_backend.repository.InvoiceItemRepository;
import ma.medisync.medisync_backend.repository.PaymentRepository;
import ma.medisync.medisync_backend.repository.PerformedActRepository;
import ma.medisync.medisync_backend.service.EmailService;
import ma.medisync.medisync_backend.service.InvoiceService;
import ma.medisync.medisync_backend.service.SecurityService;
import ma.medisync.medisync_backend.service.ApiResponseMapper;
import ma.medisync.medisync_backend.dto.ApiResponses.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class BillingController {

    private final InvoiceService invoiceService;
    private final InvoiceItemRepository itemRepository;
    private final PaymentRepository paymentRepository;
    private final PerformedActRepository actRepository;
    private final SecurityService securityService;
    private final EmailService emailService;
    private final ApiResponseMapper mapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('SECRETARY', 'ADMIN')")
    public ResponseEntity<InvoiceResponse> create(@RequestBody Invoice invoice) {
        return ResponseEntity.status(201).body(mapper.invoice(invoiceService.createInvoice(invoice)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SECRETARY', 'ADMIN')")
    public List<InvoiceResponse> all() {
        return invoiceService.getAllInvoices().stream().map(mapper::invoice).toList();
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'SECRETARY', 'ADMIN')")
    public InvoiceResponse byId(@PathVariable Long id) {
        Invoice invoice = invoice(id);
        securityService.assertCanAccessPatientProfile(invoice.getPatient().getId());
        return mapper.invoice(invoice);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'SECRETARY', 'ADMIN')")
    public List<InvoiceResponse> byPatient(@PathVariable Long patientId) {
        securityService.assertCanAccessPatientProfile(patientId);
        return invoiceService.getPatientInvoices(patientId).stream().map(mapper::invoice).toList();
    }

    @PostMapping("/{id}/items")
    @PreAuthorize("hasAnyRole('SECRETARY', 'ADMIN')")
    public ResponseEntity<InvoiceItemResponse> addItem(@PathVariable Long id, @RequestBody InvoiceItem item) {
        item.setInvoice(invoice(id));
        return ResponseEntity.status(201).body(mapper.invoiceItem(itemRepository.save(item)));
    }

    @GetMapping("/{id}/items")
    @PreAuthorize("hasAnyRole('PATIENT', 'SECRETARY', 'ADMIN')")
    public List<InvoiceItemResponse> items(@PathVariable Long id) {
        byId(id);
        return itemRepository.findByInvoiceId(id).stream().map(mapper::invoiceItem).toList();
    }

    @PostMapping("/{id}/payments")
    @PreAuthorize("hasAnyRole('SECRETARY', 'ADMIN')")
    public ResponseEntity<PaymentResponse> addPayment(@PathVariable Long id, @RequestBody Payment payment) {
        payment.setInvoice(invoice(id));
        payment.setPaymentDate(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);
        invoiceService.markAsPaid(id);
        return ResponseEntity.status(201).body(mapper.payment(saved));
    }

    @PostMapping("/{id}/acts")
    @PreAuthorize("hasAnyRole('DOCTOR', 'SECRETARY', 'ADMIN')")
    public ResponseEntity<PerformedActResponse> addAct(@PathVariable Long id, @RequestBody PerformedAct act) {
        act.setAppointment(invoice(id).getAppointment());
        return ResponseEntity.status(201).body(mapper.performedAct(actRepository.save(act)));
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('PATIENT', 'SECRETARY', 'ADMIN')")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) throws Exception {
        Invoice invoice = invoice(id);
        securityService.assertCanAccessPatientProfile(invoice.getPatient().getId());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();
        document.add(new Paragraph("MediSync Invoice " + invoice.getInvoiceNumber()));
        document.add(new Paragraph("Patient: " + invoice.getPatient().getFullName()));
        document.add(new Paragraph("Total: " + invoice.getTotalAmount()));
        document.add(new Paragraph("Status: " + invoice.getStatus()));
        document.close();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"invoice-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(output.toByteArray());
    }

    @PostMapping("/{id}/email")
    @PreAuthorize("hasAnyRole('SECRETARY', 'ADMIN')")
    public ResponseEntity<Void> email(@PathVariable Long id) {
        Invoice invoice = invoice(id);
        emailService.sendBestEffort(invoice.getPatient().getEmail(), "MediSync invoice " + invoice.getInvoiceNumber(),
                "Your invoice total is " + invoice.getTotalAmount() + ". Status: " + invoice.getStatus());
        return ResponseEntity.accepted().build();
    }

    private Invoice invoice(Long id) {
        return invoiceService.getInvoiceById(id)
                .orElseThrow(() -> ResourceNotFoundException.invoiceNotFound(id));
    }
}
