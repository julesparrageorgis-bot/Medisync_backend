package ma.medisync.medisync_backend.service;

import ma.medisync.medisync_backend.dto.DashboardResponse;
import ma.medisync.medisync_backend.repository.AppointmentRepository;
import ma.medisync.medisync_backend.repository.InvoiceRepository;
import ma.medisync.medisync_backend.repository.PaymentRepository;
import ma.medisync.medisync_backend.repository.ConsultationRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ConsultationRoomRepository roomRepository;

    public DashboardResponse getStats() {
        DashboardResponse resp = new DashboardResponse();
        long totalAppointments = appointmentRepository.count();
        long activeRooms = roomRepository.countByIsActiveTrue();
        long availableRooms = roomRepository.countByIsActiveTrueAndIsAvailableTrue();
        resp.setTotalAppointments(totalAppointments);
        resp.setOccupancyRate(activeRooms == 0 ? 0 : (activeRooms - availableRooms) * 100.0 / activeRooms);
        resp.setNoShowRate(totalAppointments == 0 ? 0 :
                appointmentRepository.countByStatus("NO_SHOW") * 100.0 / totalAppointments);
        BigDecimal revenue = paymentRepository.sumAllPayments();
        resp.setTotalRevenue(revenue);
        resp.setUnpaidInvoices(invoiceRepository.findByIsPaidFalseAndDueDateBefore(LocalDate.MAX).size());
        resp.setConsultationsPerDoctor(appointmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(appointment -> appointment.getDoctor().getId(), Collectors.counting())));
        return resp;
    }
}
