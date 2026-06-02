package ma.medisync.medisync_backend.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.dto.DashboardResponse;
import ma.medisync.medisync_backend.service.DashboardService;
import ma.medisync.medisync_backend.service.MonthlyFinancialReportService;
import ma.medisync.medisync_backend.dto.ApiResponses.MonthlyFinancialReportResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class FinancialReportController {

    private final DashboardService dashboardService;
    private final MonthlyFinancialReportService monthlyReportService;

    @GetMapping("/financial")
    public DashboardResponse financial() {
        return dashboardService.getStats();
    }

    @GetMapping("/financial.pdf")
    public ResponseEntity<byte[]> pdf() throws Exception {
        DashboardResponse report = dashboardService.getStats();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();
        document.add(new Paragraph("MediSync Financial Report"));
        document.add(new Paragraph("Revenue: " + report.getTotalRevenue()));
        document.add(new Paragraph("Unpaid invoices: " + report.getUnpaidInvoices()));
        document.add(new Paragraph("No-show rate: " + report.getNoShowRate() + "%"));
        document.close();
        return download(output.toByteArray(), "medisync-financial-report.pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/financial.xlsx")
    public ResponseEntity<byte[]> excel() throws Exception {
        DashboardResponse report = dashboardService.getStats();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet("Financial report");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Revenue");
            header.createCell(1).setCellValue("Unpaid invoices");
            header.createCell(2).setCellValue("No-show rate");
            Row values = sheet.createRow(1);
            values.createCell(0).setCellValue(report.getTotalRevenue().doubleValue());
            values.createCell(1).setCellValue(report.getUnpaidInvoices());
            values.createCell(2).setCellValue(report.getNoShowRate());
            workbook.write(output);
        }
        return download(output.toByteArray(), "medisync-financial-report.xlsx",
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @GetMapping("/monthly")
    public List<MonthlyFinancialReportResponse> monthlyReports() {
        return monthlyReportService.findAll();
    }

    @PostMapping("/monthly/generate")
    public MonthlyFinancialReportResponse generateMonthlyReport(
            @RequestParam(required = false) String month) {
        return monthlyReportService.generate(month == null ? YearMonth.now() : YearMonth.parse(month));
    }

    @GetMapping("/monthly/{id}")
    public MonthlyFinancialReportResponse monthlyReport(@PathVariable Long id) {
        return monthlyReportService.findById(id);
    }

    @GetMapping("/monthly/{id}.pdf")
    public ResponseEntity<byte[]> monthlyPdf(@PathVariable Long id) throws Exception {
        MonthlyFinancialReportResponse report = monthlyReportService.findById(id);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();
        document.add(new Paragraph("MediSync Monthly Financial Report " + report.reportMonth()));
        document.add(new Paragraph("Revenue: " + report.totalRevenue()));
        document.add(new Paragraph("Unpaid invoices: " + report.unpaidInvoices()));
        document.add(new Paragraph("Appointments: " + report.totalAppointments()));
        document.add(new Paragraph("No-show rate: " + report.noShowRate() + "%"));
        document.close();
        return download(output.toByteArray(), "medisync-financial-report-" + report.reportMonth() + ".pdf",
                MediaType.APPLICATION_PDF);
    }

    @GetMapping("/monthly/{id}.xlsx")
    public ResponseEntity<byte[]> monthlyExcel(@PathVariable Long id) throws Exception {
        MonthlyFinancialReportResponse report = monthlyReportService.findById(id);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet("Monthly report");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Month");
            header.createCell(1).setCellValue("Revenue");
            header.createCell(2).setCellValue("Unpaid invoices");
            header.createCell(3).setCellValue("Appointments");
            Row values = sheet.createRow(1);
            values.createCell(0).setCellValue(report.reportMonth());
            values.createCell(1).setCellValue(report.totalRevenue().doubleValue());
            values.createCell(2).setCellValue(report.unpaidInvoices());
            values.createCell(3).setCellValue(report.totalAppointments());
            workbook.write(output);
        }
        return download(output.toByteArray(), "medisync-financial-report-" + report.reportMonth() + ".xlsx",
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    private ResponseEntity<byte[]> download(byte[] bytes, String fileName, MediaType mediaType) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(mediaType)
                .body(bytes);
    }
}
