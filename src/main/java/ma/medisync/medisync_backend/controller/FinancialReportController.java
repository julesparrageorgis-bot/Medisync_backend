package ma.medisync.medisync_backend.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.dto.DashboardResponse;
import ma.medisync.medisync_backend.service.DashboardService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class FinancialReportController {

    private final DashboardService dashboardService;

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

    private ResponseEntity<byte[]> download(byte[] bytes, String fileName, MediaType mediaType) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(mediaType)
                .body(bytes);
    }
}
