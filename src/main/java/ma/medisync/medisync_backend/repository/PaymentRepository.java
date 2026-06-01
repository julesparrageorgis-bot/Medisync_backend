package ma.medisync.medisync_backend.repository;

import ma.medisync.medisync_backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM Payment p")
    BigDecimal sumAllPayments();
}