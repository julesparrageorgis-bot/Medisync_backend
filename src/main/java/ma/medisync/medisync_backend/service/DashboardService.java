package ma.medisync.medisync_backend.service;

import ma.medisync.medisync_backend.dto.DashboardResponse;
import ma.medisync.medisync_backend.repository.AppointmentRepository;
import ma.medisync.medisync_backend.repository.InvoiceRepository;
import ma.medisync.medisync_backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class DashboardService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    public DashboardResponse getStats() {
        DashboardResponse resp = new DashboardResponse();
        long totalAppointments = appointmentRepository.count();
        long completed = appointmentRepository.countByStatus("COMPLETED");
        resp.setOccupancyRate((double) completed / totalAppointments * 100);
        resp.setNoShowRate(appointmentRepository.countByStatus("NO_SHOW") * 100.0 / totalAppointments);
        BigDecimal revenue = paymentRepository.sumAllPayments();
        resp.setTotalRevenue(revenue);
        return resp;
    }
}